#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
ENTRYPOINT="$PROJECT_DIR/dist/index.js"

print_usage() {
  cat <<'USAGE'
Usage: processor.sh <kotlin-directory> [--tags-source <path>] [--output-file <path>]

Inputs:
  <kotlin-directory>   Root directory to scan for .kt files (required)
  --tags-source <path> Optional Kotlin file or directory with tag constants
  --output-file <path> Optional output file (defaults to stdout)
USAGE
}

if [ "${1:-}" = "-h" ] || [ "${1:-}" = "--help" ]; then
  print_usage
  exit 0
fi

has_target=0
for arg in "$@"; do
  if [[ "$arg" != -* ]]; then
    has_target=1
    break
  fi
done

if [ "$has_target" -eq 0 ]; then
  print_usage
  exit 1
fi

(
  cd "$PROJECT_DIR"
  npm run build
)

if [ ! -f "$ENTRYPOINT" ]; then
  echo "Build failed: missing $ENTRYPOINT" >&2
  exit 1
fi

node "$ENTRYPOINT" "$@"
