package com.ideabonyan.iranapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.BuildConfig;
import com.ideabonyan.iranapp.Components.MyButton;
import com.ideabonyan.iranapp.Components.MyCheckbox;
import com.ideabonyan.iranapp.Components.MyEdittextView;
import com.ideabonyan.iranapp.Components.MyRadioButton;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.JalaliDate;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * منو > دیگر > قرارداد الکترونیک: the paper contract businesses used to sign, filled in and
 * submitted from the app. An admin approves it (the user becomes pro) or rejects it with a reason,
 * after which the user corrects the form here and sends it again.
 *
 * The wording is downloaded from the server (App\Support\EContractTemplate) rather than kept in
 * the app, so the text previewed here is exactly what gets stored and shown to the admin.
 */
public class EContractActivity extends AppCompatActivity implements Get_Insert_Edit_Data {

    private static final int REQUEST_LOAD = 1;
    private static final int REQUEST_SUBMIT = 2;

    private static final String HIGHLIGHT_COLOR = "#b71c1c";
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\w+)\\}");

    ProgressBar loading, submitProgress;
    LinearLayout errorView, form, submitBox;
    ScrollView scroll;
    CardView statusCard;
    MyTextView statusTitle, statusText, dateTXT, titleTXT, contractText, contractTextHint, startDateTXT, durationTXT, endDateTXT;
    RadioGroup businessTypeGroup, managerTitleGroup;
    MyEdittextView businessName, managerName, nationalCode, phone, mobile, address, subject, discount;
    MyCheckbox acceptChk;
    MyButton submitBTN;

    Typeface font;

    // From /api/e-contracts/current.
    JSONArray sections = new JSONArray();
    String blank = "..............";
    String today;
    int[] todayDate;
    int[] durations = {3, 6, 12, 24, 36};

    // Form state that is not held by a view.
    int[] startDate;
    int durationMonths = 12;
    String submitLabel = "ارسال درخواست قرارداد";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_contract);

        font = Typeface.createFromAsset(getAssets(), "IRANYekanRegularMobile(FaNum).ttf");
        initializer();
        onClicks();
        loadCurrent();
    }

    private void initializer() {
        loading = findViewById(R.id.eContractLoading);
        errorView = findViewById(R.id.eContractError);
        scroll = findViewById(R.id.eContractScroll);
        statusCard = findViewById(R.id.eContractStatusCard);
        statusTitle = findViewById(R.id.eContractStatusTitle);
        statusText = findViewById(R.id.eContractStatusText);
        titleTXT = findViewById(R.id.eContractTitle);
        dateTXT = findViewById(R.id.eContractDate);
        form = findViewById(R.id.eContractForm);
        businessTypeGroup = findViewById(R.id.eContractBusinessTypeGroup);
        managerTitleGroup = findViewById(R.id.eContractManagerTitleGroup);
        businessName = findViewById(R.id.eContractBusinessName);
        managerName = findViewById(R.id.eContractManagerName);
        nationalCode = findViewById(R.id.eContractNationalCode);
        phone = findViewById(R.id.eContractPhone);
        mobile = findViewById(R.id.eContractMobile);
        address = findViewById(R.id.eContractAddress);
        subject = findViewById(R.id.eContractSubject);
        startDateTXT = findViewById(R.id.eContractStartDate);
        durationTXT = findViewById(R.id.eContractDuration);
        endDateTXT = findViewById(R.id.eContractEndDate);
        discount = findViewById(R.id.eContractDiscount);
        contractText = findViewById(R.id.eContractText);
        contractTextHint = findViewById(R.id.eContractTextHint);
        submitBox = findViewById(R.id.eContractSubmitBox);
        acceptChk = findViewById(R.id.eContractAcceptChk);
        submitBTN = findViewById(R.id.eContractSubmitBTN);
        submitProgress = findViewById(R.id.eContractSubmitProgress);
    }

    private void onClicks() {
        findViewById(R.id.newAdBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.eContractRetryBTN).setOnClickListener(v -> loadCurrent());
        startDateTXT.setOnClickListener(v -> showStartDatePicker());
        durationTXT.setOnClickListener(v -> showDurationPicker());
        submitBTN.setOnClickListener(v -> {
            if (validate()) submit();
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePreview();
            }
        };
        for (EditText e : new EditText[]{businessName, managerName, nationalCode, phone, mobile, address, subject, discount}) {
            e.addTextChangedListener(watcher);
        }
        businessTypeGroup.setOnCheckedChangeListener((group, checkedId) -> updatePreview());
        managerTitleGroup.setOnCheckedChangeListener((group, checkedId) -> updatePreview());
    }

    ////////////////////////////////////////
    ////    LOADING
    ////////////////////////////////////////

    private void loadCurrent() {
        loading.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(this, new HashMap<>(), withToken(StaticData.E_CONTRACT_CURRENT), Request.Method.GET, REQUEST_LOAD);
    }

    private void onLoaded(JSONObject json) throws JSONException {
        new UserSessionManager(this).setUserRole(json.optString("role", "normal"));

        today = json.getString("today");
        todayDate = JalaliDate.parse(today);

        JSONObject template = json.getJSONObject("template");
        sections = template.getJSONArray("sections");
        blank = template.optString("blank", blank);
        titleTXT.setText(template.optString("title", "قرارداد همکاری"));

        JSONObject options = json.getJSONObject("options");
        fillRadioGroup(businessTypeGroup, options.getJSONArray("business_types"));
        fillRadioGroup(managerTitleGroup, options.getJSONArray("manager_titles"));
        JSONArray durationsJson = options.getJSONArray("durations");
        durations = new int[durationsJson.length()];
        for (int i = 0; i < durations.length; i++) durations[i] = durationsJson.getInt(i);

        JSONObject contract = json.isNull("contract") ? null : json.getJSONObject("contract");
        String status = contract == null ? "none" : contract.getString("status");

        switch (status) {
            case "pending":
                showReadOnly(contract, "#FFA000", "در انتظار بررسی",
                        "درخواست قرارداد شما در تاریخ " + contract.optString("submitted_at") + " ارسال شد و در حال بررسی توسط کارشناسان ایران اپ است.\n"
                                + "نتیجه بررسی از طریق اعلان به شما اطلاع داده می شود.");
                break;
            case "approved":
                showReadOnly(contract, "#388E3C", "قرارداد شما تایید شد",
                        "قرارداد شما در تاریخ " + contract.optString("reviewed_at") + " تایید و ثبت شد و حساب کاربری شما به «کاربر پرو» ارتقا یافت.");
                break;
            case "rejected":
                showStatus(HIGHLIGHT_COLOR, "قرارداد شما نیاز به اصلاح دارد",
                        "دلیل: " + contract.optString("rejection_reason") + "\n\n"
                                + "لطفا اطلاعات قرارداد را بازبینی و اصلاح کنید و دوباره ارسال نمایید.");
                showForm(contract);
                break;
            default:
                statusCard.setVisibility(View.GONE);
                showForm(null);
        }

        scroll.setVisibility(View.VISIBLE);
    }

    private void fillRadioGroup(RadioGroup group, JSONArray items) throws JSONException {
        group.removeAllViews();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            RadioButton rb = new MyRadioButton(this);
            rb.setId(View.generateViewId());
            rb.setTag(item.getString("key"));
            rb.setText(item.getString("label"));
            rb.setTypeface(font);
            rb.setTextSize(14);
            rb.setButtonTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT, 1f);
            group.addView(rb, lp);
        }
    }

    /** Pending or approved: the stored contract, no form. */
    private void showReadOnly(JSONObject contract, String color, String title, String text) {
        showStatus(color, title, text);
        form.setVisibility(View.GONE);
        submitBox.setVisibility(View.GONE);
        contractTextHint.setVisibility(View.GONE);
        dateTXT.setText("تاریخ: " + contract.optString("contract_date") + "        شماره قرارداد: " + contract.optInt("id"));
        contractText.setText(withoutHeader(contract.optString("contract_text")));
    }

    /**
     * The stored text opens with the title and date ("قرارداد همکاری", "تاریخ: ..."), which the
     * header card above already shows; drop them so they are not printed twice.
     */
    private static String withoutHeader(String text) {
        String[] blocks = text.split("\n\n", 3);
        if (blocks.length == 3 && blocks[1].startsWith("تاریخ:")) return blocks[2];
        return text;
    }

    private void showStatus(String color, String title, String text) {
        statusCard.setVisibility(View.VISIBLE);
        statusTitle.setBackgroundColor(Color.parseColor(color));
        statusTitle.setText(title);
        statusText.setText(text);
    }

    /** A new contract, or the rejected one pre-filled for correction. */
    private void showForm(JSONObject previous) {
        form.setVisibility(View.VISIBLE);
        submitBox.setVisibility(View.VISIBLE);
        contractTextHint.setVisibility(View.VISIBLE);
        dateTXT.setText("تاریخ: " + today);
        acceptChk.setChecked(false);
        submitLabel = previous == null ? "ارسال درخواست قرارداد" : "ارسال مجدد درخواست قرارداد";
        submitBTN.setText(submitLabel);

        startDate = todayDate;
        if (previous != null) {
            checkByTag(businessTypeGroup, previous.optString("business_type"));
            checkByTag(managerTitleGroup, previous.optString("manager_title"));
            businessName.setText(previous.optString("business_name"));
            managerName.setText(previous.optString("manager_name"));
            nationalCode.setText(previous.optString("national_code"));
            phone.setText(previous.optString("phone"));
            mobile.setText(previous.optString("mobile"));
            address.setText(previous.optString("address"));
            subject.setText(previous.optString("subject"));
            discount.setText(previous.optString("discount_percent"));
            durationMonths = previous.optInt("duration_months", 12);
            // A start date that has passed while the request was being reviewed moves up to today.
            int[] previousStart = JalaliDate.parse(previous.optString("start_date"));
            if (previousStart != null && compare(previousStart, todayDate) > 0) startDate = previousStart;
        } else {
            User user = UserHelper.LoadUserInfo(this);
            if (mobile.getText().length() == 0 && user.getPhone() != null) mobile.setText(user.getPhone());
        }
        updateDateFields();
        updatePreview();
    }

    private void checkByTag(RadioGroup group, String tag) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (tag.equals(child.getTag())) group.check(child.getId());
        }
    }

    ////////////////////////////////////////
    ////    DATE AND DURATION
    ////////////////////////////////////////

    private void updateDateFields() {
        startDateTXT.setText(JalaliDate.format(startDate));
        durationTXT.setText(durationMonths + " ماه");
        endDateTXT.setText("تاریخ پایان قرارداد: " + JalaliDate.format(JalaliDate.addMonths(startDate, durationMonths)));
    }

    private void showStartDatePicker() {
        final NumberPicker year = new NumberPicker(this);
        final NumberPicker month = new NumberPicker(this);
        final NumberPicker day = new NumberPicker(this);

        year.setMinValue(todayDate[0]);
        year.setMaxValue(todayDate[0] + 1);
        year.setValue(startDate[0]);
        month.setMinValue(1);
        month.setMaxValue(12);
        month.setDisplayedValues(JalaliDate.MONTH_NAMES);
        month.setValue(startDate[1]);
        day.setMinValue(1);
        day.setMaxValue(JalaliDate.monthLength(startDate[0], startDate[1]));
        day.setValue(startDate[2]);

        NumberPicker.OnValueChangeListener clampDay = (picker, oldVal, newVal) ->
                day.setMaxValue(JalaliDate.monthLength(year.getValue(), month.getValue()));
        year.setOnValueChangedListener(clampDay);
        month.setOnValueChangedListener(clampDay);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        row.setPadding(20, 30, 20, 10);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        row.addView(day, lp);
        row.addView(month, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.4f));
        row.addView(year, lp);

        new AlertDialog.Builder(this)
                .setTitle("تاریخ شروع قرارداد")
                .setView(row)
                .setPositiveButton("تایید", (dialog, which) -> {
                    int[] picked = {year.getValue(), month.getValue(), day.getValue()};
                    if (compare(picked, todayDate) < 0) {
                        ShowToast.failure("تاریخ شروع قرارداد نمی تواند پیش از امروز باشد", EContractActivity.this);
                        return;
                    }
                    startDate = picked;
                    updateDateFields();
                    updatePreview();
                })
                .setNegativeButton("انصراف", null)
                .show();
    }

    private void showDurationPicker() {
        String[] labels = new String[durations.length];
        for (int i = 0; i < durations.length; i++) labels[i] = durations[i] + " ماه";

        new AlertDialog.Builder(this)
                .setTitle("مدت قرارداد")
                .setItems(labels, (dialog, which) -> {
                    durationMonths = durations[which];
                    updateDateFields();
                    updatePreview();
                })
                .show();
    }

    private static int compare(int[] a, int[] b) {
        for (int i = 0; i < 3; i++) if (a[i] != b[i]) return Integer.compare(a[i], b[i]);
        return 0;
    }

    ////////////////////////////////////////
    ////    LIVE PREVIEW
    ////////////////////////////////////////

    private Map<String, String> values() {
        Map<String, String> v = new HashMap<>();
        v.put("business_type", checkedLabel(businessTypeGroup));
        v.put("business_name", text(businessName));
        v.put("manager_title", checkedLabel(managerTitleGroup));
        v.put("manager_name", text(managerName));
        v.put("national_code", text(nationalCode));
        v.put("phone", text(phone));
        v.put("mobile", text(mobile));
        v.put("address", text(address));
        v.put("subject", text(subject));
        v.put("start_date", startDate == null ? "" : JalaliDate.format(startDate));
        v.put("end_date", startDate == null ? "" : JalaliDate.format(JalaliDate.addMonths(startDate, durationMonths)));
        v.put("duration", durationMonths + " ماه");
        v.put("discount_percent", text(discount));
        return v;
    }

    /** The contract with the user's values in red, the way it will be stored. */
    private void updatePreview() {
        if (form.getVisibility() != View.VISIBLE || sections.length() == 0) return;

        Map<String, String> v = values();
        StringBuilder html = new StringBuilder();
        for (int i = 0; i < sections.length(); i++) {
            JSONObject section = sections.optJSONObject(i);
            if (section == null) continue;
            if (html.length() > 0) html.append("<br><br>");
            if (!section.isNull("heading")) {
                html.append("<b><font color='").append(HIGHLIGHT_COLOR).append("'>")
                        .append(TextUtils.htmlEncode(section.optString("heading"))).append(":</font></b><br>");
            }
            Matcher m = PLACEHOLDER.matcher(section.optString("text"));
            int last = 0;
            while (m.find()) {
                html.append(TextUtils.htmlEncode(section.optString("text").substring(last, m.start())));
                String value = v.get(m.group(1));
                if (value == null || value.isEmpty()) {
                    html.append(TextUtils.htmlEncode(blank));
                } else {
                    html.append("<b><font color='").append(HIGHLIGHT_COLOR).append("'>")
                            .append(TextUtils.htmlEncode(value)).append("</font></b>");
                }
                last = m.end();
            }
            html.append(TextUtils.htmlEncode(section.optString("text").substring(last)));
        }
        contractText.setText(Html.fromHtml(html.toString().replace("\n", "<br>"), Html.FROM_HTML_MODE_LEGACY));
    }

    private String checkedLabel(RadioGroup group) {
        View checked = group.findViewById(group.getCheckedRadioButtonId());
        return checked instanceof RadioButton ? ((RadioButton) checked).getText().toString() : "";
    }

    private String checkedKey(RadioGroup group) {
        View checked = group.findViewById(group.getCheckedRadioButtonId());
        return checked != null && checked.getTag() != null ? checked.getTag().toString() : "";
    }

    private static String text(EditText e) {
        return JalaliDate.toLatinDigits(e.getText().toString().trim());
    }

    ////////////////////////////////////////
    ////    VALIDATION AND SUBMIT
    ////////////////////////////////////////

    /** Same rules as the server (EContractController::rules), so most mistakes are caught here. */
    private boolean validate() {
        if (checkedKey(businessTypeGroup).isEmpty())
            return fail("نوع کسب و کار (شرکت، موسسه، مجموعه یا فروشگاه) را انتخاب کنید", null);
        if (text(businessName).length() < 2)
            return fail("نام شرکت / موسسه / مجموعه / فروشگاه را وارد کنید", businessName);
        if (checkedKey(managerTitleGroup).isEmpty())
            return fail("عنوان مدیر (آقا یا خانم) را انتخاب کنید", null);
        if (text(managerName).length() < 3)
            return fail("نام و نام خانوادگی مدیر را وارد کنید", managerName);
        if (!isValidNationalCode(text(nationalCode)))
            return fail("کد ملی وارد شده معتبر نیست", nationalCode);
        if (!text(phone).matches("^0\\d{9,10}$"))
            return fail("شماره تلفن ثابت را همراه با کد شهر وارد کنید", phone);
        if (!text(mobile).matches("^09\\d{9}$"))
            return fail("شماره همراه باید 11 رقم و با 09 شروع شود", mobile);
        if (text(address).length() < 10)
            return fail("آدرس را کامل وارد کنید", address);
        if (text(subject).length() < 2)
            return fail("خدمات / محصولات موضوع قرارداد را وارد کنید", subject);
        int percent;
        try {
            percent = Integer.parseInt(text(discount));
        } catch (NumberFormatException e) {
            percent = 0;
        }
        if (percent < 1 || percent > 100)
            return fail("درصد تخفیف باید بین 1 تا 100 باشد", discount);
        if (!acceptChk.isChecked())
            return fail("برای ارسال قرارداد باید متن آن را مطالعه کرده و شرایط را بپذیرید", null);
        return true;
    }

    private boolean fail(String message, EditText field) {
        ShowToast.failure(message, this);
        if (field != null) {
            field.requestFocus();
            scroll.post(() -> scroll.smoothScrollTo(0, Math.max(0, field.getTop() - 100)));
        }
        return false;
    }

    /** Iranian national ID checksum, as on the server. */
    static boolean isValidNationalCode(String code) {
        if (!code.matches("^\\d{10}$") || code.matches("^(\\d)\\1{9}$")) return false;
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (code.charAt(i) - '0') * (10 - i);
        int remainder = sum % 11;
        int check = code.charAt(9) - '0';
        return remainder < 2 ? check == remainder : check == 11 - remainder;
    }

    private void submit() {
        submitBTN.setText("");
        submitBTN.setEnabled(false);
        submitProgress.setVisibility(View.VISIBLE);

        Map<String, String> params = new HashMap<>();
        params.put("business_type", checkedKey(businessTypeGroup));
        params.put("business_name", text(businessName));
        params.put("manager_title", checkedKey(managerTitleGroup));
        params.put("manager_name", text(managerName));
        params.put("national_code", text(nationalCode));
        params.put("phone", text(phone));
        params.put("mobile", text(mobile));
        params.put("address", text(address));
        params.put("subject", text(subject));
        params.put("start_date", JalaliDate.format(startDate));
        params.put("duration_months", String.valueOf(durationMonths));
        params.put("discount_percent", text(discount));
        params.put("terms_accepted", acceptChk.isChecked() ? "1" : "0");
        params.put("app_version", BuildConfig.VERSION_NAME);

        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(this, params, withToken(StaticData.E_CONTRACT_SUBMIT), Request.Method.POST, REQUEST_SUBMIT);
    }

    private void resetSubmitButton() {
        submitProgress.setVisibility(View.GONE);
        submitBTN.setEnabled(true);
        submitBTN.setText(submitLabel);
    }

    private String withToken(String url) {
        String token = new UserSessionManager(this).getLoginToken();
        try {
            return url + "?token=" + URLEncoder.encode(token, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return url + "?token=" + token;
        }
    }

    ////////////////////////////////////////
    ////    RESPONSES
    ////////////////////////////////////////

    @Override
    public void on_volley_response(String response, int id) {
        if (isFinishing()) return;
        try {
            JSONObject json = new JSONObject(response);
            String status = json.optString("status");

            if (id == REQUEST_LOAD) {
                loading.setVisibility(View.GONE);
                if ("200".equals(status)) {
                    onLoaded(json);
                } else {
                    errorView.setVisibility(View.VISIBLE);
                }
                return;
            }

            resetSubmitButton();
            switch (status) {
                case "201":
                    ShowToast.success("درخواست قرارداد شما ارسال شد و پس از بررسی، نتیجه به شما اعلام می شود", this);
                    scroll.scrollTo(0, 0);
                    loadCurrent();
                    break;
                case "422":
                    JSONArray errors = json.optJSONArray("errors");
                    StringBuilder message = new StringBuilder();
                    for (int i = 0; errors != null && i < errors.length(); i++) {
                        message.append("• ").append(errors.getString(i)).append("\n");
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("لطفا موارد زیر را اصلاح کنید")
                            .setMessage(message.toString().trim())
                            .setPositiveButton("باشه", null)
                            .show();
                    break;
                case "409":
                    // Already pending or approved (e.g. a retried request): show the current state.
                    ShowToast.failure(json.optString("message", "درخواست شما پیش از این ثبت شده است"), this);
                    loadCurrent();
                    break;
                default:
                    ShowToast.failure("لطفا دوباره تلاش کنید", this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (id == REQUEST_LOAD) {
                loading.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            } else {
                resetSubmitButton();
                ShowToast.failure("لطفا دوباره تلاش کنید", this);
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {
        if (isFinishing()) return;
        if (id == REQUEST_LOAD) {
            loading.setVisibility(View.GONE);
            scroll.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            resetSubmitButton();
            ShowToast.failure("ارسال ممکن نشد؛ لطفا اتصال اینترنت خود را بررسی کنید", this);
        }
    }

    /** Opens the contract screen, sending guests to log in first. */
    public static void open(Activity activity) {
        User user = UserHelper.LoadUserInfo(activity);
        if (!user.isLoggedIn()) {
            ShowToast.failure("برای ثبت قرارداد الکترونیک ابتدا وارد حساب کاربری خود شوید", activity);
            activity.startActivity(new Intent(activity, LoginActivity.class));
        } else if (!user.isVerrified()) {
            activity.startActivity(new Intent(activity, ConfirmationActivity.class));
        } else {
            activity.startActivity(new Intent(activity, EContractActivity.class));
        }
    }
}
