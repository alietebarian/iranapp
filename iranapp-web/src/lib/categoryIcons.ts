import { CATEGORY_STYLES, SUB_CATEGORY_STYLES, type IconStyle } from '@/content/categoryIconTables';

/**
 * Maps category and sub category names onto the app's line icons (CategoryIcons.java).
 *
 * Categories are typed by an admin, who may attach any thumbnail or none. To keep the grid
 * consistent, a name that matches a known family gets that family's icon and the server
 * thumbnail is only a fallback.
 */

export const DEFAULT_ICON = 'ic_cat_default';

/**
 * Names are typed by hand, so they mix Arabic and Persian letter shapes and stray spaces /
 * half spaces. Fold those away before matching keywords.
 */
export function normalizeName(name: string): string {
  let out = '';
  for (const ch of name) {
    switch (ch) {
      case 'ي':
        out += 'ی';
        break;
      case 'ك':
        out += 'ک';
        break;
      case 'ة':
        out += 'ه';
        break;
      case 'أ':
      case 'إ':
      case 'آ':
        out += 'ا';
        break;
      case '‌': // zero width non joiner
      case 'ً':
      case 'ٌ':
      case 'ٍ': // tanvin
      case 'َ':
      case 'ُ':
      case 'ِ': // harakat
      case 'ّ':
      case 'ْ':
        break;
      default:
        if (!/\s/.test(ch)) out += ch;
    }
  }
  return out;
}

function prepare(table: IconStyle[]): IconStyle[] {
  return table.map((style) => ({ icon: style.icon, keywords: style.keywords.map(normalizeName) }));
}

const categories = prepare(CATEGORY_STYLES);
const subCategories = prepare(SUB_CATEGORY_STYLES);

function match(table: IconStyle[], name: string | null | undefined): string | null {
  if (!name) return null;
  const normalized = normalizeName(name);
  if (!normalized) return null;
  for (const style of table) {
    for (const keyword of style.keywords) {
      if (normalized.includes(keyword)) return style.icon;
    }
  }
  return null;
}

/** The icon of a category, or null when the name belongs to no known family. */
export function categoryIcon(name: string | null | undefined): string | null {
  return match(categories, name);
}

/**
 * The icon of a sub category: its own when the name says what it is, otherwise the icon of the
 * category it was opened from, otherwise the default.
 */
export function subCategoryIcon(name: string | null | undefined, parentIcon: string | null): string {
  return match(subCategories, name) ?? parentIcon ?? DEFAULT_ICON;
}
