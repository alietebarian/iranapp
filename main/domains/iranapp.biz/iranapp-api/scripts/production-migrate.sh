#!/bin/bash
# Runs, one file at a time, the migrations the production database (inherited from
# ghollacLaravel) is missing. The 2026_09_09_1413xx baseline files describe tables production
# already has, so a plain `php artisan migrate` would stop on "table already exists".
#
# Every file here only adds tables or nullable/defaulted columns, so the old backend keeps
# working after it runs. A file that already ran is skipped, so the script is safe to re-run.
#
#   bash scripts/production-migrate.sh
#   PHP_BIN=/opt/alt/php83/usr/bin/php bash scripts/production-migrate.sh   # other PHP version

set -e
cd "$(dirname "$0")/.."

PHP_BIN="${PHP_BIN:-/opt/alt/php84/usr/bin/php}"

MIGRATIONS=(
    2026_09_09_143234_create_personal_access_tokens_table.php
    2026_09_12_120000_add_video_to_ads_table.php
    2026_09_13_120000_create_ads_view_table.php
    2026_09_13_130000_create_admin_notifications_table.php
    2026_09_21_120000_create_ad_terms_acceptances_table.php
    2026_09_24_120000_add_publish_at_to_news_table.php
    2026_09_26_120000_add_role_to_users_table.php
    2026_09_26_120100_create_e_contracts_table.php
    2026_09_27_120000_create_app_installs_table.php
    2026_09_27_130000_add_birth_date_to_users_table.php
    2026_09_27_140000_convert_ad_tables_to_utf8mb4.php
    2026_09_27_150000_create_membership_cards_table.php
    2026_09_30_120000_create_e_contract_templates_table.php
    2026_09_30_120000_create_phone_book_entries_table.php
    2026_09_30_130000_add_billing_to_e_contracts_table.php
    2026_09_30_130100_add_e_contract_to_admin_notifications_table.php
    2026_10_01_120000_add_national_code_to_membership_cards_table.php
    2026_10_04_120000_fix_membership_card_serial_typo.php
)

echo "PHP: $("$PHP_BIN" -r 'echo PHP_VERSION;')"
for m in "${MIGRATIONS[@]}"; do
    echo "== $m"
    "$PHP_BIN" artisan migrate --force --path="database/migrations/$m"
done
echo "All ${#MIGRATIONS[@]} migrations done."
