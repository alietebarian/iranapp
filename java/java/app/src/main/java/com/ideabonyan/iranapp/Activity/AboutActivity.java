package com.ideabonyan.iranapp.Activity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.ShowToast;

/**
 * درباره ما, shown natively inside the app (it used to open public_html/about/ in a web view).
 * Reached from the «درباره ما» tile on the landing page and from منو > درباره ما.
 *
 * The wording follows public_html/about/index.html; when one changes, update the other.
 */
public class AboutActivity extends AppCompatActivity {

    /** What tapping a row does. */
    private enum Action { NONE, COPY, OPEN }

    private LinearLayout licences, contacts;
    private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        font = Typeface.createFromAsset(getAssets(), "IRANYekanRegularMobile(FaNum).ttf");

        findViewById(R.id.aboutBackBTN).setOnClickListener(v -> finish());
        licences = findViewById(R.id.aboutLicences);
        contacts = findViewById(R.id.aboutContacts);
        ((TextView) findViewById(R.id.aboutFooter)).setText("ایران آپ · iranapp.biz");

        addChips("پروانه کسب", "ثبت شرکت", "مجوز ارشاد");

        addRow(licences, R.drawable.ic_done_black_24dp, "پروانه کسب", "اتحادیه فناوری اطلاعات رایانه", Action.NONE, null, false);
        addRow(licences, R.drawable.ic_done_black_24dp, "پروانه کسب", "کانون آگهی تبلیغات", Action.NONE, null, false);
        addRow(licences, R.drawable.ic_done_black_24dp, "ثبت شرکت - شماره ثبت", "54916", Action.COPY, null, true);
        addRow(licences, R.drawable.ic_done_black_24dp, "مجوز فعالیت از وزارت فرهنگ و ارشاد اسلامی - شماره مجوز", "1-1-718196-63-5-1", Action.COPY, null, true);

        addRow(contacts, R.drawable.ic_phone_black_24dp, "شماره تماس", "09380052014", Action.OPEN, "tel:09380052014", false);
        addRow(contacts, R.drawable.ic_phone_black_24dp, "شماره تماس", "09131094406", Action.OPEN, "tel:09131094406", false);
        addRow(contacts, R.drawable.ic_instagram_black_24dp, "اینستاگرام", "@iranianappes", Action.OPEN, "https://instagram.com/iranianappes", false);
        addRow(contacts, R.drawable.ic_eitaa, "کانال ایتا", "@iranapp", Action.OPEN, "https://eitaa.com/iranapp", false);
        addRow(contacts, R.drawable.ic_email_black_24dp, "پیامک نظرات، انتقادات و پیشنهادات", "985000538027", Action.OPEN, "sms:985000538027", false);
        addRow(contacts, R.drawable.ic_location_on_black_24dp, "کد پستی", "8157834911", Action.COPY, null, false);
        addRow(contacts, R.drawable.ic_web_black_24dp, "وب‌سایت", "iranapp.biz", Action.OPEN, "http://iranapp.biz", false);
        addRow(contacts, R.drawable.ic_cat_store, "دانلود و امتیاز به ایران آپ", "کافه‌بازار", Action.OPEN, "https://cafebazaar.ir/app/com.ideabonyan.iranapp/?l=fa", false);
    }

    private void addChips(String... labels) {
        LinearLayout chips = findViewById(R.id.aboutChips);
        for (String label : labels) {
            TextView chip = new TextView(this);
            chip.setText(label);
            chip.setTypeface(font);
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            chip.setTextColor(ContextCompat.getColor(this, R.color.home_text_muted));
            chip.setBackgroundResource(R.drawable.bg_about_chip);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(12), dp(6), dp(12), dp(6));
            chip.setCompoundDrawablePadding(dp(4));
            chip.setCompoundDrawablesRelative(tinted(R.drawable.ic_done_black_24dp, 14), null, null, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginStart(dp(4));
            lp.setMarginEnd(dp(4));
            chips.addView(chip, lp);
        }
    }

    /**
     * @param target for OPEN: a tel:, sms: or web address
     * @param pill   show the value as a highlighted number, like the web page does
     */
    private void addRow(LinearLayout card, int icon, String label, final String value,
                        Action action, final String target, boolean pill) {
        if (card.getChildCount() > 0) {
            View divider = new View(this);
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.home_tile_stroke));
            card.addView(divider, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        }

        View row = LayoutInflater.from(this).inflate(R.layout.item_about_row, card, false);
        ImageView iconView = row.findViewById(R.id.aboutRowIcon);
        iconView.setImageResource(icon);
        // Eitaa's logo is multi-coloured; the accent tint would flatten it into a red square.
        if (icon == R.drawable.ic_eitaa) iconView.setImageTintList(null);
        ((TextView) row.findViewById(R.id.aboutRowLabel)).setText(label);
        TextView valueView = row.findViewById(R.id.aboutRowValue);
        valueView.setText(value);
        if (pill) {
            valueView.setBackgroundResource(R.drawable.bg_about_pill);
            valueView.setTextColor(ContextCompat.getColor(this, R.color.home_accent));
            valueView.setPadding(dp(10), dp(2), dp(10), dp(2));
        }

        ImageView hint = row.findViewById(R.id.aboutRowAction);
        if (action == Action.OPEN) {
            hint.setImageResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
            row.setOnClickListener(v -> open(target));
        } else if (action == Action.COPY) {
            hint.setImageResource(R.drawable.ic_content_copy);
            row.setOnClickListener(v -> copy(label, value));
        } else {
            hint.setVisibility(View.INVISIBLE);
            row.setClickable(false);
        }
        // Any value can be copied with a long press, including phone numbers and links.
        if (action != Action.NONE) {
            row.setOnLongClickListener(v -> {
                copy(label, value);
                return true;
            });
        }

        card.addView(row);
    }

    private void open(String target) {
        Intent intent;
        if (target.startsWith("tel:")) {
            intent = new Intent(Intent.ACTION_DIAL, Uri.parse(target));
        } else if (target.startsWith("sms:")) {
            intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(target.replace("sms:", "smsto:")));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(target));
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ShowToast.failure("برنامه ای برای باز کردن این مورد پیدا نشد", this);
        }
    }

    private void copy(String label, String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) return;
        clipboard.setPrimaryClip(ClipData.newPlainText(label, value));
        ShowToast.success("کپی شد: " + value, this);
    }

    private android.graphics.drawable.Drawable tinted(int res, int sizeDp) {
        android.graphics.drawable.Drawable d = ContextCompat.getDrawable(this, res).mutate();
        d.setTint(ContextCompat.getColor(this, R.color.home_accent));
        d.setBounds(0, 0, dp(sizeDp), dp(sizeDp));
        return d;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
