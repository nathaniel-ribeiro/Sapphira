import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import smile.anomaly.IsolationForest
import smile.data.DataFrame
import smile.feature.imputation.KNNImputer

@Serializable
class ScreeningModel(@Contextual val imputer : KNNImputer? = null,
                     @Contextual val forest : IsolationForest? = null) {
    fun fit(data: Array<DoubleArray>) : ScreeningModel{
        val numCols = data[0].size
        val df = DataFrame.of(data, *(0 until numCols).map { "feature_$it" }.toTypedArray())
        val fittedImputer = KNNImputer(df, 5)
        val imputedData = fittedImputer.apply(df).toArray()
        val fittedForest = IsolationForest.fit(imputedData)
        return ScreeningModel(fittedImputer, fittedForest)
    }

    fun predict(data : Array<DoubleArray>) : DoubleArray{
        requireNotNull(imputer){"Must call fit() before predict(). Are you sure you are using a fitted ScreeningModel?"}
        requireNotNull(forest){"Must call fit() before predict(). Are you sure you are using a fitted ScreeningModel?"}
        val numCols = data[0].size
        val df = DataFrame.of(data, *(0 until numCols).map { "feature_$it" }.toTypedArray())
        val imputedData = imputer.apply(df).toArray()
        return forest.score(imputedData)
    }

    companion object{
        const val serialVersionUID = 42L
    }
}