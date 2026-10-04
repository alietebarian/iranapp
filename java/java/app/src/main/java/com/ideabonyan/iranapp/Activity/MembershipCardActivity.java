package com.ideabonyan.iranapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.BuildConfig;
import com.ideabonyan.iranapp.Components.MyButton;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * منو > دیگر > کارت عضویت ("کارت هدیه معرفی به مراکز طرف قرارداد"). The user requests a card
 * for 1 to 6 members; an admin approves it (this screen then shows the card) or rejects it with a
 * note, after which the user corrects the request here and sends it again.
 *
 * The membership date is the day of the request and the card is valid for a year; both, and the
 * serial number, are set by the server (App\Http\Controllers\MembershipCardController).
 */
public class MembershipCardActivity extends AppCompatActivity implements Get_Insert_Edit_Data {

    private static final int REQUEST_LOAD = 1;
    private static final int REQUEST_SUBMIT = 2;

    private static final String HIGHLIGHT_COLOR = "#b71c1c";

    ProgressBar loading, submitProgress;
    LinearLayout errorView, card, form, membersBox;
    ScrollView scroll;
    CardView statusCard;
    MyTextView pageTitle, statusTitle, statusText, cardStamp, cardLabel, cardMembers, cardDate, cardExpiry,
            formDate, formExpiry, formSerial, membersLabel;
    TextView cardSerial;
    MyButton addMemberBTN, submitBTN;
    // National code of the first member (the card's holder); the others only give names.
    EditText nationalCode;

    // From /api/membership-card.
    int minMembers = 1;
    int maxMembers = 6;
    int validityMonths = 12;
    String holder = "";

    String submitLabel = "ثبت درخواست کارت عضویت";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_card);

        initializer();
        onClicks();
        loadCurrent();
    }

    private void initializer() {
        loading = findViewById(R.id.membershipLoading);
        errorView = findViewById(R.id.membershipError);
        scroll = findViewById(R.id.membershipScroll);
        pageTitle = findViewById(R.id.membershipPageTitle);
        statusCard = findViewById(R.id.membershipStatusCard);
        statusTitle = findViewById(R.id.membershipStatusTitle);
        statusText = findViewById(R.id.membershipStatusText);
        card = findViewById(R.id.membershipCard);
        cardStamp = findViewById(R.id.membershipCardStamp);
        cardLabel = findViewById(R.id.membershipCardLabel);
        cardSerial = findViewById(R.id.membershipCardSerial);
        cardMembers = findViewById(R.id.membershipCardMembers);
        cardDate = findViewById(R.id.membershipCardDate);
        cardExpiry = findViewById(R.id.membershipCardExpiry);
        form = findViewById(R.id.membershipForm);
        formDate = findViewById(R.id.membershipFormDate);
        formExpiry = findViewById(R.id.membershipFormExpiry);
        formSerial = findViewById(R.id.membershipFormSerial);
        membersLabel = findViewById(R.id.membershipMembersLabel);
        membersBox = findViewById(R.id.membershipMembers);
        addMemberBTN = findViewById(R.id.membershipAddMemberBTN);
        nationalCode = findViewById(R.id.membershipNationalCode);
        submitBTN = findViewById(R.id.membershipSubmitBTN);
        submitProgress = findViewById(R.id.membershipSubmitProgress);
    }

    private void onClicks() {
        findViewById(R.id.newAdBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.membershipRetryBTN).setOnClickListener(v -> loadCurrent());
        addMemberBTN.setOnClickListener(v -> {
            EditText added = addMemberRow("");
            added.requestFocus();
        });
        submitBTN.setOnClickListener(v -> {
            if (validate()) submit();
        });
    }

    ////////////////////////////////////////
    ////    LOADING
    ////////////////////////////////////////

    private void loadCurrent() {
        loading.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(this, new HashMap<>(), withToken(StaticData.MEMBERSHIP_CARD), Request.Method.GET, REQUEST_LOAD);
    }

    private void onLoaded(JSONObject json) throws JSONException {
        pageTitle.setText(json.optString("title", pageTitle.getText().toString()));
        minMembers = json.optInt("min_members", minMembers);
        maxMembers = json.optInt("max_members", maxMembers);
        validityMonths = json.optInt("validity_months", validityMonths);
        holder = json.optString("holder", "");
        membersLabel.setText("نام اعضا (حداقل " + minMembers + " و حداکثر " + maxMembers + " نفر)");

        String today = json.getString("today");
        JSONObject existing = json.isNull("card") ? null : json.getJSONObject("card");
        String status = existing == null ? "none" : existing.getString("status");

        switch (status) {
            case "pending":
                showStatus("#FFA000", "در انتظار بررسی",
                        "درخواست کارت عضویت شما در تاریخ " + existing.optString("submitted_at") + " ارسال شد و در حال بررسی توسط کارشناسان ایران اپ است.\n"
                                + "نتیجه بررسی از طریق اعلان به شما اطلاع داده می شود.");
                showCard(existing, "در انتظار تایید");
                form.setVisibility(View.GONE);
                break;
            case "approved":
                if (existing.optBoolean("is_expired")) {
                    showStatus("#616161", "اعتبار کارت به پایان رسیده است",
                            "اعتبار کارت عضویت شما در تاریخ " + existing.optString("expiry_date") + " به پایان رسیده است.");
                    showCard(existing, "منقضی شده");
                } else {
                    statusCard.setVisibility(View.GONE);
                    showCard(existing, null);
                }
                form.setVisibility(View.GONE);
                break;
            case "rejected":
                showStatus(HIGHLIGHT_COLOR, "درخواست شما نیاز به اصلاح دارد",
                        "توضیحات کارشناس: " + existing.optString("rejection_reason") + "\n\n"
                                + "لطفا موارد گفته شده را اصلاح کنید و درخواست را دوباره ارسال نمایید.");
                card.setVisibility(View.GONE);
                showForm(today, existing);
                break;
            default:
                statusCard.setVisibility(View.GONE);
                card.setVisibility(View.GONE);
                showForm(today, null);
        }

        scroll.setVisibility(View.VISIBLE);
    }

    private void showStatus(String color, String title, String text) {
        statusCard.setVisibility(View.VISIBLE);
        statusTitle.setBackgroundColor(Color.parseColor(color));
        statusTitle.setText(title);
        statusText.setText(text);
    }

    /** The card; greyed out with a stamp while pending or once expired (stamp != null). */
    private void showCard(JSONObject data, String stamp) {
        card.setVisibility(View.VISIBLE);
        card.setBackgroundResource(stamp == null ? R.drawable.bg_membership_card : R.drawable.bg_membership_card_inactive);
        cardStamp.setVisibility(stamp == null ? View.GONE : View.VISIBLE);
        cardLabel.setVisibility(stamp == null ? View.VISIBLE : View.GONE);
        if (stamp != null) cardStamp.setText(stamp);

        cardSerial.setText(data.optString("serial_number"));
        cardDate.setText(data.optString("membership_date"));
        cardExpiry.setText(data.optString("expiry_date"));

        StringBuilder members = new StringBuilder();
        JSONArray list = data.optJSONArray("members");
        for (int i = 0; list != null && i < list.length(); i++) {
            if (members.length() > 0) members.append("\n");
            members.append(i + 1).append(". ").append(list.optString(i));
        }
        cardMembers.setText(members.toString());
    }

    /** A new request, or the rejected one pre-filled for correction. */
    private void showForm(String today, JSONObject previous) {
        form.setVisibility(View.VISIBLE);
        submitLabel = previous == null ? "ثبت درخواست کارت عضویت" : "ارسال مجدد درخواست";
        submitBTN.setText(submitLabel);

        // The server sets the real dates when the request arrives; these are what it will set today.
        formDate.setText(today);
        int[] start = JalaliDate.parse(today);
        formExpiry.setText(start == null ? "" : "اعتبار کارت: یک سال، تا " + JalaliDate.format(JalaliDate.addMonths(start, validityMonths)));
        // The isolate keeps the groups in order ("1394 0010 ...") inside the right-to-left field.
        formSerial.setText(previous == null
                ? "پس از ثبت درخواست به شما اختصاص داده می شود"
                : "⁦" + previous.optString("serial_number") + "⁩");

        nationalCode.setText(previous == null || previous.isNull("national_code") ? "" : previous.optString("national_code"));
        membersBox.removeAllViews();
        JSONArray list = previous == null ? null : previous.optJSONArray("members");
        if (list != null && list.length() > 0) {
            for (int i = 0; i < list.length() && i < maxMembers; i++) addMemberRow(list.optString(i));
        } else {
            addMemberRow(holder);
        }
    }

    ////////////////////////////////////////
    ////    MEMBER ROWS
    ////////////////////////////////////////

    private EditText addMemberRow(String name) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_membership_member, membersBox, false);
        EditText input = row.findViewById(R.id.membershipMemberName);
        input.setText(name);
        row.findViewById(R.id.membershipMemberRemove).setOnClickListener(v -> {
            membersBox.removeView(row);
            refreshMemberRows();
        });
        membersBox.addView(row);
        refreshMemberRows();
        return input;
    }

    /** Numbers the hints, and hides "add" at the maximum and "remove" at the minimum. */
    private void refreshMemberRows() {
        int count = membersBox.getChildCount();
        for (int i = 0; i < count; i++) {
            View row = membersBox.getChildAt(i);
            ((EditText) row.findViewById(R.id.membershipMemberName)).setHint("نام و نام خانوادگی عضو " + (i + 1));
            row.findViewById(R.id.membershipMemberRemove).setVisibility(count > minMembers ? View.VISIBLE : View.INVISIBLE);
        }
        addMemberBTN.setVisibility(count < maxMembers ? View.VISIBLE : View.GONE);
    }

    private List<EditText> memberInputs() {
        List<EditText> inputs = new ArrayList<>();
        for (int i = 0; i < membersBox.getChildCount(); i++) {
            inputs.add(membersBox.getChildAt(i).findViewById(R.id.membershipMemberName));
        }
        return inputs;
    }

    private static String text(EditText e) {
        return e.getText().toString().trim().replaceAll("\\s+", " ");
    }

    ////////////////////////////////////////
    ////    VALIDATION AND SUBMIT
    ////////////////////////////////////////

    /** Same rules as the server: 1 to 6 names of at least 3 characters; empty rows are skipped. */
    private boolean validate() {
        int filled = 0;
        List<EditText> inputs = memberInputs();
        for (int i = 0; i < inputs.size(); i++) {
            String name = text(inputs.get(i));
            if (name.isEmpty()) continue;
            if (name.length() < 3) return fail("نام و نام خانوادگی عضو " + (i + 1) + " را کامل وارد کنید", inputs.get(i));
            filled++;
        }
        if (filled < minMembers) {
            return fail("نام دست کم یک عضو را وارد کنید", inputs.isEmpty() ? null : inputs.get(0));
        }
        if (filled > maxMembers) return fail("حداکثر " + maxMembers + " عضو را می توانید ثبت کنید", null);
        String code = JalaliDate.toLatinDigits(nationalCode.getText().toString().trim());
        if (code.isEmpty()) return fail("کد ملی عضو اول (صاحب کارت) را وارد کنید", nationalCode);
        if (!EContractActivity.isValidNationalCode(code)) return fail("کد ملی عضو اول (صاحب کارت) معتبر نیست", nationalCode);
        return true;
    }

    private boolean fail(String message, EditText field) {
        ShowToast.failure(message, this);
        if (field != null) {
            field.requestFocus();
            View anchor = field == nationalCode ? (View) nationalCode.getParent().getParent() : membersBox;
            scroll.post(() -> scroll.smoothScrollTo(0, Math.max(0, form.getTop() + anchor.getTop() - 100)));
        }
        return false;
    }

    private void submit() {
        submitBTN.setText("");
        submitBTN.setEnabled(false);
        submitProgress.setVisibility(View.VISIBLE);

        Map<String, String> params = new HashMap<>();
        int index = 0;
        for (EditText input : memberInputs()) {
            String name = text(input);
            if (!name.isEmpty()) params.put("members[" + (index++) + "]", name);
        }
        params.put("national_code", JalaliDate.toLatinDigits(nationalCode.getText().toString().trim()));
        params.put("app_version", BuildConfig.VERSION_NAME);

        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(this, params, withToken(StaticData.MEMBERSHIP_CARD), Request.Method.POST, REQUEST_SUBMIT);
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
                    ShowToast.success("درخواست کارت عضویت شما ثبت شد و پس از بررسی، نتیجه به شما اعلام می شود", this);
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

    /** Opens the membership card screen, sending guests to log in first. */
    public static void open(Activity activity) {
        User user = UserHelper.LoadUserInfo(activity);
        if (!user.isLoggedIn()) {
            ShowToast.failure("برای دریافت کارت عضویت ابتدا وارد حساب کاربری خود شوید", activity);
            activity.startActivity(new Intent(activity, LoginActivity.class));
        } else if (!user.isVerrified()) {
            activity.startActivity(new Intent(activity, ConfirmationActivity.class));
        } else {
            activity.startActivity(new Intent(activity, MembershipCardActivity.class));
        }
    }
}
