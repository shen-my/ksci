package ksci

object Normalization {

    /**
     * 根据数据最大值和最小值进行标准化
     */
    fun uniform(values: Collection<Float>, lower: Float? = null, upper: Float? = null): List<Float> {
        var min = lower
        var max = upper

        if (values.isEmpty()) return emptyList()

        if (min == null || max == null) {
            for (v in values) {
                if (min == null || v < min) min = v
                if (max == null || v > max) max = v
            }
        }

        if (min == max)
            return List(values.size) { 1f }

        return values.map { (it - min!!) / (max!! - min) }
    }

    fun zScore(values: Collection<Float>): List<Float> {
        if (values.isEmpty()) return emptyList()
        val std = Statistic.std(values)
        if (std == 0f)
            return List(values.size) { 0f }
        val mean = Statistic.mean(values)
        return values.map { (it - mean) / std }
    }
}
