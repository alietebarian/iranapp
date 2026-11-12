"""One-off importer: pulls the icons, images, fonts and long texts of the Android app
(../java/java/app/src/main) into this project so the web version looks the same.

Run from the project root:  python scripts/import-android-assets.py
"""
import os
import re
import shutil
import sys
import xml.etree.ElementTree as ET

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MAIN = os.path.normpath(os.path.join(ROOT, '..', 'java', 'java', 'app', 'src', 'main'))
RES = os.path.join(MAIN, 'res')
A = '{http://schemas.android.com/apk/res/android}'


def color(value):
    """#AARRGGBB / #RRGGBB / #RGB -> (svg colour, opacity)."""
    if not value or not value.startswith('#'):
        return 'none', 1
    v = value[1:]
    if len(v) in (3, 4):
        v = ''.join(c * 2 for c in v)
    alpha = 1.0
    if len(v) == 8:
        alpha = int(v[:2], 16) / 255
        v = v[2:]
    if alpha == 0:
        return 'none', 1
    return '#' + v.upper(), round(alpha, 3)


def convert_node(node):
    out = []
    for child in node:
        tag = child.tag
        if tag == 'path':
            attrs = ['d="%s"' % child.get(A + 'pathData')]
            fill, fill_opacity = color(child.get(A + 'fillColor'))
            attrs.append('fill="%s"' % fill)
            if fill != 'none' and fill_opacity != 1:
                attrs.append('fill-opacity="%s"' % fill_opacity)
            if child.get(A + 'fillType') == 'evenOdd':
                attrs.append('fill-rule="evenodd"')
            stroke, _ = color(child.get(A + 'strokeColor'))
            if stroke != 'none':
                attrs.append('stroke="%s"' % stroke)
                attrs.append('stroke-width="%s"' % (child.get(A + 'strokeWidth') or '1'))
                if child.get(A + 'strokeLineCap'):
                    attrs.append('stroke-linecap="%s"' % child.get(A + 'strokeLineCap'))
                if child.get(A + 'strokeLineJoin'):
                    attrs.append('stroke-linejoin="%s"' % child.get(A + 'strokeLineJoin'))
            out.append('<path %s/>' % ' '.join(attrs))
        elif tag == 'group':
            transforms = []
            tx, ty = child.get(A + 'translateX'), child.get(A + 'translateY')
            if tx or ty:
                transforms.append('translate(%s %s)' % (tx or 0, ty or 0))
            sx, sy = child.get(A + 'scaleX'), child.get(A + 'scaleY')
            if sx or sy:
                transforms.append('scale(%s %s)' % (sx or 1, sy or 1))
            inner = convert_node(child)
            out.append('<g transform="%s">%s</g>' % (' '.join(transforms), inner) if transforms else inner)
    return ''.join(out)


def import_vectors():
    src = os.path.join(RES, 'drawable')
    dst = os.path.join(ROOT, 'src', 'assets', 'icons')
    count = 0
    for name in sorted(os.listdir(src)):
        if not name.endswith('.xml'):
            continue
        try:
            root = ET.parse(os.path.join(src, name)).getroot()
        except ET.ParseError:
            continue
        if root.tag != 'vector':
            continue
        w, h = root.get(A + 'viewportWidth'), root.get(A + 'viewportHeight')
        svg = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 %s %s">%s</svg>\n' % (w, h, convert_node(root))
        with open(os.path.join(dst, name[:-4] + '.svg'), 'w', encoding='utf-8') as f:
            f.write(svg)
        count += 1
    print('vectors:', count)


IMAGES = [
    'iran_app_logo1.png', 'iran_app_logo_red.png', 'decagram.png', 'user1.png', 'heart.png', 'hearts.png',
    'newspaper.png', 'magnify.png', 'pending.png', 'place_holder.jpg', 'place_holder_car.jpg',
    'place_holder_home.jpg', 'place_holder_job.jpg', 'intro1.jpg', 'intro2.jpg', 'intro3.jpg',
    'h1.jpg', 'h2.jpg', 'h3.jpg', 'h4.jpg', 'h5.jpg', 'h9.jpg', 'h10.jpg', 'ic_gift_box_ube.png',
    'map_layout_pointer.png', 'car1.png', 'worker.png', 'aparteman.png', 'km.png', 'engin.png',
    'chassis.png', 'date_create.png', 'meter.png', 'bedroom.png', 'education.png', 'expertise.png',
    'contract.png', 'hiring.png', 'eraa.png', 'home.png', 'menu.png',
]
XX_IMAGES = ['kasbokar.png', 'account.png']


def import_images():
    dst = os.path.join(ROOT, 'src', 'assets', 'img')
    missing = []
    for name in IMAGES:
        path = os.path.join(RES, 'drawable', name)
        if os.path.exists(path):
            shutil.copy2(path, os.path.join(dst, name))
        else:
            missing.append(name)
    for name in XX_IMAGES:
        path = os.path.join(RES, 'drawable-xxhdpi', name)
        if os.path.exists(path):
            shutil.copy2(path, os.path.join(dst, name))
        else:
            missing.append(name)
    launcher = os.path.join(RES, 'mipmap-xxxhdpi', 'ic_launcher.png')
    if os.path.exists(launcher):
        shutil.copy2(launcher, os.path.join(dst, 'ic_launcher.png'))
    print('images copied; missing:', missing)


def import_fonts():
    dst = os.path.join(ROOT, 'src', 'assets', 'fonts')
    assets = os.path.join(MAIN, 'assets')
    shutil.copy2(os.path.join(assets, 'IRANYekanRegularMobile(FaNum).ttf'), os.path.join(dst, 'IRANYekanFaNum.ttf'))
    shutil.copy2(os.path.join(assets, 'b_sina_bold.ttf'), os.path.join(dst, 'BSinaBold.ttf'))
    print('fonts copied')


def ts_string(text):
    return '`' + text.replace('\\', '\\\\').replace('`', '\\`').replace('${', '\\${') + '`'


def import_texts():
    xml = open(os.path.join(RES, 'values', 'strings.xml'), encoding='utf-8').read()

    def grab(name):
        m = re.search(r'<string name="%s">(.*?)</string>' % name, xml, re.S)
        text = m.group(1).strip()
        cdata = re.match(r'<!\[CDATA\[(.*)\]\]>', text, re.S)
        if cdata:
            text = cdata.group(1).strip()
        body = re.search(r'<body[^>]*>(.*)</body>', text, re.S)
        if body:
            text = body.group(1).strip()
        return text.replace("\\'", "'").replace('\\n', '\n')

    out = [
        "// Generated by scripts/import-android-assets.py from the Android app's strings.xml.",
        '',
        '/** «قوانین و مقررات» (HTML). */',
        'export const RULES_HTML = %s;' % ts_string(grab('rouls')),
        '',
        '/** «ارتباط با ما» (HTML). */',
        'export const LINK_TO_US_HTML = %s;' % ts_string(grab('link_to_us')),
        '',
        'export const ABOUT_COMPANY_TEXT = %s;' % ts_string(grab('about_company_text')),
        '',
    ]
    with open(os.path.join(ROOT, 'src', 'content', 'texts.ts'), 'w', encoding='utf-8') as f:
        f.write('\n'.join(out))
    print('texts written')


def import_category_icons():
    path = os.path.join(MAIN, 'java', 'com', 'ideabonyan', 'iranapp', 'Utils', 'CategoryIcons.java')
    java = open(path, encoding='utf-8').read()

    def table(name):
        block = re.search(r'private static final Style\[\] %s = \{(.*?)\n    \};' % name, java, re.S).group(1)
        rows = []
        for m in re.finditer(r'new Style\(R\.drawable\.(\w+),(.*?)\),', block, re.S):
            words = re.findall(r'"([^"]+)"', m.group(2))
            rows.append("  { icon: '%s', keywords: [%s] }," % (m.group(1), ', '.join("'%s'" % w for w in words)))
        return '\n'.join(rows)

    out = [
        '// Generated by scripts/import-android-assets.py from CategoryIcons.java of the Android app.',
        '// Keyword tables that pick an icon for a category / sub category name.',
        '',
        'export interface IconStyle {',
        '  icon: string;',
        '  keywords: string[];',
        '}',
        '',
        'export const CATEGORY_STYLES: IconStyle[] = [',
        table('STYLES'),
        '];',
        '',
        'export const SUB_CATEGORY_STYLES: IconStyle[] = [',
        table('SUB_STYLES'),
        '];',
        '',
    ]
    with open(os.path.join(ROOT, 'src', 'content', 'categoryIconTables.ts'), 'w', encoding='utf-8') as f:
        f.write('\n'.join(out))
    print('category icon tables written')


if __name__ == '__main__':
    if not os.path.isdir(MAIN):
        sys.exit('Android sources not found at ' + MAIN)
    os.makedirs(os.path.join(ROOT, 'src', 'content'), exist_ok=True)
    import_vectors()
    import_images()
    import_fonts()
    import_texts()
    import_category_icons()
