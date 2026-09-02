package com.ideabonyan.iranapp.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.Edit_Job;
import com.ideabonyan.iranapp.Activity.LoginActivity;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Fragment.Account.My_Job;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data4;
import com.ideabonyan.iranapp.Models.Job;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.wefor.circularanim.CircularAnim;


public class My_Job_List_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Get_Insert_Edit_Data {
    ProgressDialog progressDialog;

    List<Job> jobs;
    Context context;
    Get_Insert_Edit_Data4 get_insert_edit_data4;

    public My_Job_List_Adapter(List<Job> jobs, Context context,Get_Insert_Edit_Data4 get_insert_edit_data4) {
        this.jobs = jobs;
        this.context = context;
        this.get_insert_edit_data4 = get_insert_edit_data4;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_my_job_in_list, parent, false);

        return new My_Job_List_Adapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final My_Job_List_Adapter.CellFeedViewHolder holder = (My_Job_List_Adapter.CellFeedViewHolder) viewHolder;

        holder.rvAdListTitle.setText(jobs.get(position).getAds_title());

       holder.rvAdListAddress.setText(jobs.get(position).getCity_name()+"،"+jobs.get(position).getRegion_name()
       +" / "+jobs.get(position).getElapsed_time());

        if (jobs.get(position).getStatus().equals("pending")) {
//            holder.pendingIdentifier.setVisibility(View.VISIBLE);
            holder.txt_statous.setBackgroundColor(Color.parseColor("#F57C00"));
            holder.txt_statous.setText("در انتظار تایید");

        } else if (jobs.get(position).getStatus().equals("rejected")) {
            holder.txt_statous.setBackgroundColor(Color.parseColor("#d32f2f"));
            holder.txt_statous.setText("رد شده");
        } else if (jobs.get(position).getStatus().equals("approved")) {
            holder.txt_statous.setBackgroundColor(Color.parseColor("#388E3C"));
            holder.txt_statous.setText("تایید شد");
        }

        if (jobs.get(position).getThumbnail_photo() != null && !jobs.get(position).getThumbnail_photo().equals(null)
                && !jobs.get(position).getThumbnail_photo().equals("null") && !jobs.get(position).getThumbnail_photo().equals("")) {
            Picasso.with(context)
                    .load(jobs.get(position).getThumbnail_photo())
                    .fit()
                    .placeholder(R.drawable.place_holder_job)
                    .into(holder.rvAdListImage);
        } else {
            if (jobs.get(position).getPhotos().size() > 0) {
                Picasso.with(context)
                        .load(jobs.get(position).getPhotos().get(0).getName())
                        .fit()
                        .placeholder(R.drawable.place_holder_job)
                        .into(holder.rvAdListImage);
            } else {
                Picasso.with(context)
                        .load(R.drawable.place_holder_job)
                        .fit()
                        .centerCrop()
                        .into(holder.rvAdListImage);
            }
        }

        String type="";
        if (jobs.get(position).getType().equals("karjoo")){
            type="آماده به کار";
        }else{
            type="استخدام";
        }
        String agremment_type="";
        if (jobs.get(position).getAgremment_type().equals("tamamvaght")){
            agremment_type=("تمام وقت");

        }else if (jobs.get(position).getAgremment_type().equals("parevaght"))
        {  agremment_type=("پروژه ای");

        }else if (jobs.get(position).getAgremment_type().equals("moshaveri"))
        {  agremment_type=("مشاوره ای");

        }else if (jobs.get(position).getAgremment_type().equals("parevaght"))
        { agremment_type=("پاره وقت");

        }
        holder.txt_cost.setText(type+" / "+agremment_type);


        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircularAnim.show(holder.lin_edit)
                        .startRadius(0)
                        .endRadius(holder.card_main.getWidth())
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                holder.lin_main.setEnabled(false);
                                holder.lin_edit.setEnabled(true);
                            }
                        });

            }
        });
        holder.lin_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircularAnim.hide(holder.lin_edit)
                        .startRadius(holder.card_main.getWidth())
                        .endRadius(0)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                holder.lin_main.setEnabled(true);
                                holder.lin_edit.setEnabled(false);
                            }
                        });

            }
        });

        holder.lin_edit_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,Edit_Job.class);
//                Edit_Job.job=jobs.get(position);
                intent.putExtra("job",jobs.get(position));
                context.startActivity(intent);
            }
        });
        holder.lin_delete_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("آیا از حذف این محصول اطمینان دارید؟")
                        .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                product_delete(products.get(position).getId());
                                delete_this_add(jobs.get(position).getId());
                            }
                        })
                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });

//        holder.txt_cost.setVisibility(View.INVISIBLE);

    }
    private void delete_this_add(String id){
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("در حال اعمال تغییرات ...");
        progressDialog.show();
        Get_Volley_Call_Back.binddata(My_Job_List_Adapter.this);
        Map<String, String> params = new HashMap<String, String>();
        String url= StaticData.employs+"/"+id+ "?token=" + new UserSessionManager(context).getLoginToken();
        Get_Volley_Call_Back.Call_Volley(context,params,url, Request.Method.DELETE,144);

    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    @Override
    public void on_volley_response(String response, int id) {

        if (id==144){
            try {
                progressDialog.dismiss();
                JSONObject jsonObject=new JSONObject(response);
                if (jsonObject.getString("status").equals("200")) {
                    My_Job.rv.setVisibility(View.GONE);
                    My_Job.progressBar.setVisibility(View.VISIBLE);
                    My_Job.offset = 0;
                    My_Job.jobs = null;
                    My_Job.getData(context,get_insert_edit_data4);
                } else if (jsonObject.getString("status").equals("401")) {
                    if (jsonObject.getString("error").equals("token_invalid")) {
                        UserHelper.RemoveUserInfo(context);
                        Intent intent = new Intent(context, LoginActivity.class);
                        ShowToast.failure("لطفا دوباره وارد حساب خود شوید",(Activity) context);
                        context.startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        ImageView rvAdListImage, pendingIdentifier;
        MyTextView  rvAdListTitle, rvAdListAddress;
        TextView txt_cost,txt_statous;
        LinearLayout lin_edit,lin_main,lin_go_back,lin_edit_product,lin_delete_product;
        CardView card_main;

        public CellFeedViewHolder(View view) {
            super(view);

            rvAdListImage = (ImageView) view.findViewById(R.id.rvAdListImage);
            rvAdListTitle = (MyTextView) view.findViewById(R.id.rvAdListTitle);
            rvAdListAddress = (MyTextView) view.findViewById(R.id.rvAdListAddress);
            pendingIdentifier = (ImageView) view.findViewById(R.id.rvAdListPendingIdentifier);

            txt_cost= (TextView) view.findViewById(R.id.txt_cost);
            txt_statous= (TextView) view.findViewById(R.id.txt_statous);
            lin_edit= (LinearLayout) view.findViewById(R.id.lin_edit);
            lin_main= (LinearLayout) view.findViewById(R.id.lin_main);
            lin_go_back= (LinearLayout) view.findViewById(R.id.lin_go_back);
            lin_edit_product= (LinearLayout) view.findViewById(R.id.lin_edit_product);
            lin_delete_product= (LinearLayout) view.findViewById(R.id.lin_delete_product);
            card_main= (CardView) view.findViewById(R.id.card_main);
        }
    }
}
