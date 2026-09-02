package com.ideabonyan.iranapp.Interface;

/**
 * Created by AliReza on 2017/10/23.
 */

public interface Select_Car_Filter {
    public void on_filter_set(String cat, int brand_id, int model_id, String chasi_type, String tolid_from
            , String tolid_to, String cost_from, String cost_to, String kilometer_from, String kilometer_to, int motor_weghit
            ,int province_id,int city_id,String order_bye,String region_id);
}
