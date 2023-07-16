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

const json = fs.readFileSync('../sample/samples.json', { encoding: 'utf-8' });
const samples = JSON.parse(json)
    .filter(sample => sample['tags'].includes('showcase'))
    .sort((lhs, rhs) => rhs.id < lhs.id)
    .reverse()
    .map(sample => {
        return {
            title: sample['title'],
            description: sample['description'],
            screenshot: screenshots[sample['id']],
            className: (() => {
                const re = /.*\.(\w+)/
                const match = sample['javaClassName'].match(re);
                return match[1];
            })()
        };
    })
    .map(sample => {
        return `<tr>
<td>
    <div>
        <h3>${sample.title}</h3>
        <span>${sample.description}</span>
        <br />
        <a href="./sample/src/main/java/io/noties/adapt/sample/samples/showcase/${sample.className}.kt">${sample.className}</a>
    </div>
</td>
<td><a href="./art/${sample.screenshot}"><img src="./art/${sample.screenshot}"></a></td>
</tr>`
    })
    .join("");

console.log(samples);
