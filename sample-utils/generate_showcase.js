const fs = require('fs');

const screenshots = {
    '20230519160703': 'ui_showcase_basic1.jpg',
    '20230531162848': 'ui_showcase_circleavatar.jpg',
    '20230520002917': 'ui_showcase_basic2.jpg',
    '20230520005529': 'ui_showcase_controlflow.jpg',
    '20230601144200': 'ui_showcase_image.jpg',
    '20230602001541': 'ui_showcase_item.jpg',
    '20230601135039': 'ui_showcase_pager.jpg',
    '20230708163019': 'ui_showcase_preview.jpg',
    '20230520013122': 'ui_showcase_reference1.jpg',
    '20230602002749': 'ui_showcase_reference2.jpg',
    '20230526030500': 'ui_showcase_shape1.jpg',
    '20230526030500': 'ui_showcase_shape2.jpg',
    '20230530152155': 'ui_showcase_shape3.jpg',
    '20230601101434': 'ui_showcase_shape4.jpg',
    '20230601102605': 'ui_showcase_shape5.jpg',
    '20230601100213': 'ui_showcase_shapecomposition.jpg',
    '20230530151621': 'ui_showcase_shapedrawable.jpg',
    '20230522113544': 'ui_showcase_style.jpg',
    '20230520122012': 'ui_showcase_text1.jpg',
    '20230520123338': 'ui_showcase_text2.jpg',
    '20230716140149': 'ui_showcase_web.jpg'
};

const processScreenshotFile = (sample) => {
    const id = sample['id'];
    const screenshot = screenshots[id];

    // check required file exists
    if (!screenshot) {
        return {
            message: "Missing required screenshot for showcase",
            ...sample
        }
    }

    // the path should be from the root
    //  but this script is not at the root...
    const path = `../assets/showcase/${screenshot}`;
    const exists = fs.existsSync(path);

    if (!exists) {
        throw {
            message: "Showcase screenshot is not found",
            path,
            ...sample
        }
    }

    return path;
};

const processSourceFile = (sample) => {
    const className = (() => {
        const re = /.*\.(\w+)/
        const match = sample['javaClassName'].match(re);
        return match[1];
    })();

    const filePath = `../sample/src/main/java/io/noties/adapt/sample/samples/showcase/${className}.kt`
    const exists = fs.existsSync(filePath);

    if (!exists) {
        throw {
            message: "Source file is not found",
            filePath,
            className,
            ...sample
        }
    }

    return {
        className,
        filePath,
    };
};

const json = fs.readFileSync('../sample/samples.json', { encoding: 'utf-8' });
const samples = JSON.parse(json)
    .filter(sample => sample['tags'].includes('showcase'))
    .sort((lhs, rhs) => rhs.id < lhs.id)
    .reverse()
    .map(sample => {

        const screenshot = processScreenshotFile(sample);
        const { className, filePath } = processSourceFile(sample);

        if (!className || !filePath) {
            throw {
                message: "Unexpected state, missing required properties",
                className,
                filePath
            }
        }

        return {
            title: sample['title'],
            description: sample['description'],
            screenshot,
            className,
            filePath,
        };
    })
    .map(sample => {
        return `
<tr>
    <td>
        <div>
            <h3>${sample.title}</h3>
            <span>${sample.description}</span>
            <br />
            <a href="${sample.filePath}">${sample.className}</a>
        </div>
    </td>
    <td><a href="${sample.screenshot}"><img src="${sample.screenshot}"></a></td>
</tr>`
    })
    .join("\n\n");


const file = '../PREVIEW_SHOWCASE.md';

const template = (() => {
    const text = fs.readFileSync(file, { encoding: 'utf-8' });

    const markerStart = '<!-- FROM HERE -->';
    const markerEnd = '<!-- TO HERE -->';

    const header = (() => {
        const index = text.indexOf(markerStart);
        // (null > -1) = true (jshit)
        if (index && index > -1) {
            // all the text above the marker
            return text.substring(0, index).trim();
        } else {
            throw {
                message: "Missing marker-start",
                markerStart
            }
        }
    })();

    const footer = (() => {
        const index = text.indexOf(markerEnd);
        if (index && index > -1) {
            return text.substring(index + markerEnd.length).trim();
        } else {
            throw {
                message: "Missing marker-end",
                markerEnd
            }
        }
    })();

    return {
        header,
        footer,
        markerStart,
        markerEnd
    };
})();

const newContent = `
${template.header}

${template.markerStart}

${samples}

${template.markerEnd}

${template.footer}
`;

fs.writeFileSync(file, newContent, { encoding: 'utf-8' });
