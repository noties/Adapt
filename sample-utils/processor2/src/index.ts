
import { promises as fs } from 'fs'
import path from 'path'

type SampleMetadata = {
  id?: string
  title?: string
  description?: string
  tags: string[]
  lastModified: string
  filePath: string
  qualifiedName: string
}

type TagMap = Record<string, string>

async function main(): Promise<void> {
  const { target, tagsSource } = parseCliArgs(process.argv.slice(2))

  if (!target) {
    console.error('Usage: npm start <kotlin-directory> [--tags-source <path>]')
    process.exitCode = 1
    return
  }

  const root = process.cwd()
  const inputDir = path.resolve(target)
  const tags = await loadTags(tagsSource)

  try {
    const kotlinFiles = await collectKotlinFiles(inputDir)
    const samples: SampleMetadata[] = []

    for (const filePath of kotlinFiles) {
      try {
        const sample = await describeFile(filePath, root, tags)
        if (sample) {
          samples.push(sample)
        }
      } catch (error) {
        console.warn(`Skipping ${filePath}: ${(error as Error).message}`)
      }
    }

    process.stdout.write(JSON.stringify(samples, null, 2))
  } catch (error) {
    console.error('Failed to collect samples:', (error as Error).message)
    process.exitCode = 1
  }
}

async function collectKotlinFiles(directory: string): Promise<string[]> {
  const entries = await fs.readdir(directory, { withFileTypes: true })

  const files: string[] = []
  for (const entry of entries) {
    const resolved = path.join(directory, entry.name)
    if (entry.isDirectory()) {
      files.push(...(await collectKotlinFiles(resolved)))
    } else if (entry.isFile() && resolved.endsWith('.kt')) {
      files.push(resolved)
    }
  }
  return files
}

async function describeFile(filePath: string, repoRoot: string, tagMap: TagMap): Promise<SampleMetadata | null> {
  const source = await fs.readFile(filePath, 'utf-8')
  const annotation = extractAdaptSample(source)
  if (!annotation) {
    return null
  }

  const stats = await fs.stat(filePath)
  const packageName = extractPackage(source) || ''
  const className = extractClassName(source, annotation.end)

  if (!className) {
    throw new Error('Unable to determine class name, skipping file')
  }

  const annotationMap = parseAnnotationBody(annotation.body)

  const sample: SampleMetadata = {
    id: parseStringValue(annotationMap['id']),
    title: parseStringValue(annotationMap['title']),
    description: parseStringValue(annotationMap['description']),
    tags: parseTags(annotationMap['tags']).map((entry) => resolveTagEntry(entry, tagMap)),
    lastModified: stats.mtime.toISOString(),
    filePath: path.relative(repoRoot, filePath),
    qualifiedName: packageName ? `${packageName}.${className}` : className,
  }

  return sample
}

function extractPackage(source: string): string | null {
  const match = source.match(/^\s*package\s+([\w.]+)/m)
  return match ? match[1] : null
}

function extractClassName(source: string, afterIndex: number): string | null {
  const snippet = source.slice(afterIndex)
  const classMatch = snippet.match(/\b(?:class|object)\s+([A-Za-z0-9_]+)/)
  return classMatch ? classMatch[1] : null
}

type AnnotationBlock = {
  body: string
  end: number
}

function extractAdaptSample(source: string): AnnotationBlock | null {
  const token = '@AdaptSample'
  const start = source.indexOf(token)
  if (start === -1) {
    return null
  }

  const openIndex = source.indexOf('(', start)
  if (openIndex === -1) {
    return null
  }

  let depth = 0
  let current = openIndex
  for (; current < source.length; current += 1) {
    const char = source[current]
    if (char === '(') {
      depth += 1
    } else if (char === ')') {
      depth -= 1
      if (depth === 0) {
        return {
          body: source.slice(openIndex + 1, current),
          end: current,
        }
      }
    }
  }

  return null
}

function parseAnnotationBody(body: string): Record<string, string> {
  const entries = splitAnnotationEntries(body)
  const map: Record<string, string> = {}
  for (const entry of entries) {
    const trimmed = entry.trim()
    if (!trimmed) {
      continue
    }
    const match = trimmed.match(/^(\w+)\s*=\s*([\s\S]+)$/)
    if (match) {
      map[match[1]] = match[2].trim()
    }
  }
  return map
}

function splitAnnotationEntries(source: string): string[] {
  const entries: string[] = []
  let depth = 0
  let inString = false
  let stringChar: string | null = null
  let escape = false
  let start = 0

  for (let i = 0; i < source.length; i += 1) {
    const char = source[i]

    if (escape) {
      escape = false
      continue
    }

    if (char === '\\') {
      escape = true
      continue
    }

    if (inString) {
      if (char === stringChar) {
        inString = false
        stringChar = null
      }
      continue
    }

    if (char === '"' || char === "'") {
      inString = true
      stringChar = char
      continue
    }

    if (char === '[') {
      depth += 1
      continue
    }

    if (char === ']') {
      depth = Math.max(0, depth - 1)
      continue
    }

    if (char === '(') {
      depth += 1
      continue
    }

    if (char === ')') {
      depth = Math.max(0, depth - 1)
      continue
    }

    if (char === ',' && depth === 0) {
      entries.push(source.slice(start, i))
      start = i + 1
    }
  }

  if (start < source.length) {
    entries.push(source.slice(start))
  }

  return entries
}

function parseStringValue(raw?: string): string | undefined {
  if (!raw) {
    return undefined
  }

  const matches: string[] = []
  const regex =
    /("""([\s\S]*?)"""|"((?:[^"\\]|\\.)*)"|'((?:[^'\\]|\\.)*)')/g
  let match: RegExpExecArray | null = null
  while ((match = regex.exec(raw)) !== null) {
    const triple = match[2]
    const double = match[3]
    const single = match[4]
    if (triple !== undefined) {
      matches.push(triple)
    } else if (double !== undefined) {
      matches.push(unescapeString(double))
    } else if (single !== undefined) {
      matches.push(unescapeString(single))
    }
  }

  if (matches.length) {
    return matches.join('')
  }

  return raw.trim()
}

function unescapeString(value: string): string {
  return value.replace(/\\(.)/g, '$1')
}

function parseTags(raw?: string): string[] {
  if (!raw) {
    return []
  }

  const trimmed = raw.trim()
  if (!trimmed.startsWith('[') || !trimmed.endsWith(']')) {
    return [trimmed]
  }

  const inner = trimmed.slice(1, -1)
  const entries = splitAnnotationEntries(inner)
  return entries
    .map((entry) => entry.trim())
    .filter(Boolean)
}

function resolveTagEntry(raw: string, tagMap: TagMap): string {
  const normalized = parseStringValue(raw) ?? raw.trim()
  const referenceMatch = normalized.match(/(?:[\w.]+)?Tags\.([A-Za-z0-9_]+)/)
  if (referenceMatch) {
    const key = referenceMatch[1]
    const mapped = tagMap[key]
    if (mapped) {
      return mapped
    }
  }
  return normalized.trim()
}

function parseCliArgs(args: string[]): { target?: string; tagsSource?: string } {
  const result: { target?: string; tagsSource?: string } = {}
  const positional: string[] = []

  for (let i = 0; i < args.length; i += 1) {
    const arg = args[i]
    if (arg.startsWith('--tags-source=')) {
      result.tagsSource = arg.split('=', 2)[1]
      continue
    }

    if (arg === '--tags-source') {
      if (i + 1 < args.length) {
        result.tagsSource = args[i + 1]
        i += 1
      }
      continue
    }

    if (arg.startsWith('-')) {
      continue
    }

    positional.push(arg)
  }

  if (positional.length) {
    result.target = positional[0]
  }

  return result
}

async function loadTags(source?: string): Promise<TagMap> {
  if (!source) {
    return {}
  }

  const resolved = path.resolve(source)
  try {
    const stats = await fs.stat(resolved)
    const files = stats.isDirectory() ? await collectKotlinFiles(resolved) : [resolved]
    const map: TagMap = {}

    for (const file of files) {
      try {
        const content = await fs.readFile(file, 'utf-8')
        Object.assign(map, parseTagConstants(content))
      } catch (error) {
        console.warn(`Unable to read tags file ${file}: ${(error as Error).message}`)
      }
    }

    return map
  } catch (error) {
    console.warn(`Unable to load tags source ${resolved}: ${(error as Error).message}`)
    return {}
  }
}

function parseTagConstants(source: string): TagMap {
  const map: TagMap = {}
  const regex = /const\s+val\s+([A-Za-z0-9_]+)\s*=\s*/g
  let match: RegExpExecArray | null = null

  while ((match = regex.exec(source)) !== null) {
    const literal = readKotlinStringLiteral(source, regex.lastIndex)
    if (!literal) {
      continue
    }

    map[match[1]] = literal.value
    regex.lastIndex = literal.end
  }

  return map
}

function readKotlinStringLiteral(source: string, startIndex: number): { value: string; end: number } | null {
  let index = startIndex
  while (index < source.length && /\s/.test(source[index])) {
    index += 1
  }

  if (source.startsWith('"""', index)) {
    const end = source.indexOf('"""', index + 3)
    if (end === -1) {
      return null
    }
    const literal = source.slice(index, end + 3)
    const value = parseStringValue(literal) ?? ''
    return { value, end: end + 3 }
  }

  const quote = source[index]
  if (quote !== '"' && quote !== "'") {
    return null
  }

  let pointer = index + 1
  while (pointer < source.length) {
    if (source[pointer] === '\\') {
      pointer += 2
      continue
    }
    if (source[pointer] === quote) {
      break
    }
    pointer += 1
  }

  if (pointer >= source.length) {
    return null
  }

  const literal = source.slice(index, pointer + 1)
  const value = parseStringValue(literal) ?? ''
  return { value, end: pointer + 1 }
}

if (require.main === module) {
  main()
}
