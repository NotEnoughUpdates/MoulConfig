package moe.nea.shale.util


fun <T> Iterable<T>.findTwoSmallest(comparator: Comparator<T>): Pair<T?, T?> {
    var smallest: T? = null
    var secondSmallest: T? = null
    for (element in this) {
        if (smallest == null || comparator.compare(element, smallest) < 0) {
            secondSmallest = smallest
            smallest = element
        } else if (secondSmallest == null || comparator.compare(element, secondSmallest) > 0) {
            secondSmallest = element
        }
    }
    return Pair(smallest, secondSmallest)
}
