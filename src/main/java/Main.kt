import org.apache.commons.text.similarity.JaroWinklerSimilarity
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

fun main(args : Array<String>){
    if(args.size != 1) throw IllegalArgumentException()
    val file = args[0]
    val df = DataFrame.readCsv(file)
    println(df.head())
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
    println(df.schema())
    for(row in df){
        println(GameImportingService().convertToListOfMoves(row["moves_raw"] as String))
    }
}