package com.ideabonyan.iranapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Fragment.Account.My_Car;
import com.ideabonyan.iranapp.Fragment.Account.My_Descount_adds;
import com.ideabonyan.iranapp.Fragment.Account.My_Home;
import com.ideabonyan.iranapp.Fragment.Account.My_Job;
import com.ideabonyan.iranapp.Fragment.Dialogs.Select_Add_Type_Dialog;
import com.ideabonyan.iranapp.Interface.LoginLogoutChangeListener;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.CustomTypefaceSpan;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity  {

    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;

    MyTextView headerWelcomeTXT, headerAccountTXT;
    ImageView headerTouchIcon;

    ImageButton optionsBTN;

    MyTextView name, number, location;
    LinearLayout addAdBTN;

    UserSessionManager userSessionManager;
    ImageButton backBTN;
    ViewGroup rootView;

    LinearLayout lin_edit,lin_exit;

    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializer();
        onClicks();

        loadUserInfo();
        setupViewPager(viewPager);
//        getData();

//        hideUnhideMenuItems();
//        changeDrawerMenuFont(navigationView.getMenu());
    }

    private void initializer() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        drawerLayout = (DrawerLayout) findViewById(R.id.dl);
//        navigationView = (NavigationView) findViewById(R.id.nvInDashboard);
//        navMenu = navigationView.getMenu();
//        View headerLayout = navigationView.getHeaderView(0);
//        headerWelcomeTXT = (MyTextView) headerLayout.findViewById(R.id.headerWelcomeTXT);
//        headerAccountTXT = (MyTextView) headerLayout.findViewById(R.id.headerAccountTXT);
//        headerTouchIcon = (ImageView) headerLayout.findViewById(R.id.headerTouchIcon);

        optionsBTN = (ImageButton) findViewById(R.id.dashboardOptionsBTN);
        name = (MyTextView) findViewById(R.id.dashboardName);
        number = (MyTextView) findViewById(R.id.dashboardNumber);
        addAdBTN = (LinearLayout) findViewById(R.id.dashboardAddAd);
        location = (MyTextView) findViewById(R.id.dashboardLocation);
        backBTN = (ImageButton) findViewById(R.id.newAdBackButton);
        rootView = (ViewGroup) findViewById(R.id.dashboard);

//        nestedScrollView = (NestedScrollView) findViewById(R.id.dashboardNestedScroll);


        userSessionManager = new UserSessionManager(DashboardActivity.this);
        lin_exit=findViewById(R.id.lin_exit);
        lin_edit=findViewById(R.id.lin_edit);

    }

    public void setupViewPager(ViewPager v){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new My_Job(), "استخدام");
        viewPagerAdapter.addFragment(new My_Home(), "املاک");
        viewPagerAdapter.addFragment(new My_Car(), "وسایل نقلیه");
        viewPagerAdapter.addFragment(new My_Descount_adds(), "کسب و کار");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);



        final Typeface face = Typeface.createFromAsset(getAssets(), "IRANYekanRegularMobile(FaNum).ttf");
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(DashboardActivity.this);
                tab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                tabTextView.setText(tab.getText());
                tabTextView.setTextColor(Color.parseColor("#ffffff"));
                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTypeface(face, Typeface.BOLD);
                    tabTextView.setTextColor(Color.parseColor("#ffffff"));
                }

            }
        }
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFF176"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                TextView text = (TextView) tab.getCustomView();

                text.setTypeface(face, Typeface.BOLD);
                text.setTextColor(Color.parseColor("#FFF9C4"));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTextColor(Color.parseColor("#ffffff"));
                text.setTypeface(face, Typeface.NORMAL);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        viewPager.setCurrentItem(3, true);
        tabLayout.setFocusable(false);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void onClicks() {

//        backBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });


        lin_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, UpdateUserInfoActivity.class);
                startActivity(i);
            }
        });lin_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(DashboardActivity.this);

                builder.setTitle("")
                        .setMessage("آیا از خروج از حسابتان اطمینان دارید؟")
                        .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                UserHelper.RemoveUserInfo(DashboardActivity.this);
                                userSessionManager.setLoginToken("0");
                                finish();
                                loginLogoutChangeListener.onLoginLogoutChangeListener();
                            }
                        })
                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //////////////////////////////////////////

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        addAdBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Select_Add_Type_Dialog select_add_type_dialog=new Select_Add_Type_Dialog();
                select_add_type_dialog.show(getSupportFragmentManager(),"select_add_type_dialog");
            }
        });


        optionsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, optionsBTN);
                popupMenu.getMenuInflater().inflate(R.menu.dashboardoptions, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.dashboardEditUserInfo:
                                Intent i = new Intent(DashboardActivity.this, UpdateUserInfoActivity.class);
//                                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(i);
                                break;
                            case R.id.dashboardLogOut:



                                AlertDialog.Builder builder;

                                builder = new AlertDialog.Builder(DashboardActivity.this);

                                builder.setTitle("")
                                        .setMessage("آیا از خروج از حسابتان اطمینان دارید؟")
                                        .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                UserHelper.RemoveUserInfo(DashboardActivity.this);
                                                userSessionManager.setLoginToken("0");
                                                finish();
                                                loginLogoutChangeListener.onLoginLogoutChangeListener();
                                            }
                                        })
                                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                //////////////////////////////////////////

                                            }
                                        });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();



                                break;
                        }

                        return true;
                    }
                });

                Menu menu = popupMenu.getMenu();

                int menuCount = menu.size();
                for (int i = 0; i < menuCount; i++){

                    MenuItem item = menu.getItem(i);
                    String settingsItemTitle = menu.getItem(i).getTitle().toString();
                    SpannableString s = new SpannableString(settingsItemTitle);

                    s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);

                    item.setTitle(s);
                }

                changeDrawerMenuFont(menu);

                popupMenu.show();
            }
        });
    }

    static LoginLogoutChangeListener loginLogoutChangeListener;


    private void loadUserInfo() {
        User user = UserHelper.LoadUserInfo(DashboardActivity.this);
        name.setText(user.getFirstname() + " " + user.getLastname());
        number.setText(user.getPhone());
        String provinceName=(userSessionManager.getProvinceName().equals("0")?"اصفهان":userSessionManager.getProvinceName());
        String cityName=(userSessionManager.getCityName().equals("0")?"اصفهان":userSessionManager.getCityName());
        location.setText(provinceName + " - " + cityName);
    }


    static public void bindData(LoginLogoutChangeListener loginLogoutChangeListener2) {
        loginLogoutChangeListener = loginLogoutChangeListener2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();

    }

    private void hideUnhideMenuItems() {
        User user = UserHelper.LoadUserInfo(DashboardActivity.this);

        headerWelcomeTXT.setText(user.getFirstname() + " " + user.getLastname() + " به قلک خوش آمدید");
        headerAccountTXT.setText("");
        headerTouchIcon.setVisibility(View.INVISIBLE);

//        navMenu.findItem(R.id.dashboard).setVisible(false);
//        navMenu.findItem(R.id.login).setVisible(false);
    }
    private void changeDrawerMenuFont(Menu mm) {
        Menu m = mm;
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "IRANYekanRegularMobile(FaNum).ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ////////////////////////////////////////
        ////        CHANGING TEXT ALIGNMENT
        ////////////////////////////////////////
//        mNewTitle.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, mNewTitle.length(), 0);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int in = item.getItemId();
//
//        switch (in) {
//            case R.id.action_search:
//                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//
//                    drawerLayout.closeDrawer(Gravity.RIGHT);
//                } else {
//                    drawerLayout.openDrawer(Gravity.RIGHT);
//                }
//                break;
//
//        }
//        return super.onOptionsItemSelected(item);
//    }



}
