import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*
import java.awt.Taskbar

fun main(args : Array<String>){
    if(args.size != 1) throw IllegalArgumentException()
    // create new dataframe for anomaly detection:
    // game_format_bullet?
    // game_format_blitz?
    // game_format_rapid?
    // game_format_slow?
    // game_format_untimed?
    // game_type,
    // end_reason,
    // username_similarity,
    // red_rating,
    // black_rating,
    // result_red_checkmate?,
    // result_red_resign?,
    // result_red_disconnect?,
    // result_red_draw?
    // result_red_timer_expired?
    // result_black_checkmate?,
    // result_black_resign?
    // result_black_disconnect?
    // result_black_draw?
    // result_black_timer_expired?
    // move_count,
    // red_think_time_mean
    // red_think_time_std
    // black_think_time_std
    // black_think_time_std
    // avg_cp_loss_red (excluding first 14 plies, endgame positions, and positions where one side has overwhelming advantage),
    // avg_cp_loss_black (excluding first 14 plies, endgame positions, and positions where one side has overwhelming advantage),
    val evaluationsRedPerspective = listOf(0.33, 0.42, 0.33, 0.83, 0.26, 2.32, 0.54, 0.81, -0.14, 0.93, 0.97, 1.02, -0.05, 0.62, 0.57, 2.27, 2.19, 4.15, 4.04, 4.7, 4.27, 5.83, 5.47, 7.25, 5.39, 8.63, 6.75, 7.67, 6.99, 7.8, 7.67, 8.26, 7.94, 8.48, 8.08, 95.0, 10.06, 96.0, 97.0, 97.0, 98.0)
    val pikafish = Pikafish(ConfigOptions)
    val featureExtractionService = FeatureExtractionService(pikafish, ConfigOptions)
    println(featureExtractionService.getAdjustedCPLosses(evaluationsRedPerspective, Alliance.BLACK))
}