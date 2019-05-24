package captainmarkov.utils

enum class Series(
    val seriesName: String,
    val urlSuffix: String
) {
    TOS("StarTrek", "episodes"),
    TNG("NextGen", "episodes"),
    DS9("DS9", "episodes"),
    VOY("Voyager", "episode_listing"),
    ENT("Enterprise", "episodes"),
    MOVIES("movies", "index");

    override fun toString(): String {
        return seriesName
    }
}