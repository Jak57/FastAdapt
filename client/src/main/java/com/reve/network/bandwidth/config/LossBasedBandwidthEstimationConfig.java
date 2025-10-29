package com.reve.network.bandwidth.config;

public class LossBasedBandwidthEstimationConfig {
    public static double min_increase_factor                                = 1.02;
    public static double max_increase_factor                                = 1.08;

    public static long increase_low_rtt                                     = 200; // ms
    public static long increase_high_rtt                                    = 800; // ms

    public static double decrease_factor                                    = 0.99;
    public static long loss_window                                          = 800; // ms
    public static long loss_max_window                                      = 800; // ms
    public static long acknowledged_rate_max_window                         = 800; // ms

//    public static long increase_offset                                      = 1000; // bps
    public static long increase_offset                                      = 1; // bps


//    public static double loss_bandwidth_balance_increase                    = 500; // bps
//    public static double loss_bandwidth_balance_decrease                    = 4000; // bps
//    public static double loss_bandwidth_balance_reset                       = 100; // bps

    public static double loss_bandwidth_balance_increase                    = 0.5; // bps
    public static double loss_bandwidth_balance_decrease                    = 4; // bps
    public static double loss_bandwidth_balance_reset                       = 0.1; // bps

    public static double loss_bandwidth_balance_exponent                    = 0.5;

    public static boolean allow_resets                                      = true;
    public static long decrease_interval                                    = 300; // ms
    public static long loss_report_timeout                                  = 6000; // ms
}
