import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

// Material symbols, inlined since compose no longer ships material-icons-core
val AddIcon = materialIcon("Add", "M19,13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z")
val CloseIcon = materialIcon(
    "Close",
    "M19,6.41L17.59,5 12,10.59 6.41,5 5,6.41 10.59,12 5,17.59 6.41,19 12,13.41 17.59,19 19,17.59 13.41,12z"
)

private fun materialIcon(name: String, pathData: String): ImageVector {
    return ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).addPath(addPathNodes(pathData), fill = SolidColor(Color.Black)).build()
}
