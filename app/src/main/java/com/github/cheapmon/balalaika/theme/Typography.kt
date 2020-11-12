package com.github.cheapmon.balalaika.theme

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.font
import androidx.compose.ui.text.font.fontFamily
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.util.DarkThemeProvider

private val roboto = fontFamily(
    font(R.font.roboto_thin, FontWeight.Thin, FontStyle.Normal),
    font(R.font.roboto_thinitalic, FontWeight.Thin, FontStyle.Italic),
    font(R.font.roboto_light, FontWeight.Light, FontStyle.Normal),
    font(R.font.roboto_lightitalic, FontWeight.Light, FontStyle.Italic),
    font(R.font.roboto_medium, FontWeight.Medium, FontStyle.Normal),
    font(R.font.roboto_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    font(R.font.roboto_regular, FontWeight.Normal, FontStyle.Normal),
    font(R.font.roboto_italic, FontWeight.Normal, FontStyle.Italic),
    font(R.font.roboto_bold, FontWeight.Bold, FontStyle.Normal),
    font(R.font.roboto_bolditalic, FontWeight.Bold, FontStyle.Italic),
    font(R.font.roboto_black, FontWeight.Black, FontStyle.Normal),
    font(R.font.roboto_blackitalic, FontWeight.Black, FontStyle.Italic)
)

private val aleo = fontFamily(
    font(R.font.aleo_light, FontWeight.Light, FontStyle.Normal),
    font(R.font.aleo_lightitalic, FontWeight.Light, FontStyle.Italic),
    font(R.font.aleo_regular, FontWeight.Normal, FontStyle.Normal),
    font(R.font.aleo_italic, FontWeight.Normal, FontStyle.Italic),
    font(R.font.aleo_bold, FontWeight.Bold, FontStyle.Normal),
    font(R.font.aleo_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

val typography = Typography(
    defaultFontFamily = aleo,
    h1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 72.sp
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 60.sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    button = TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )
)

@Preview
@Composable
fun TypographyPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Surface {
            Column {
                Text(text = "Headline 1", style = MaterialTypography.h1)
                Text(text = "Headline 2", style = MaterialTypography.h2)
                Text(text = "Headline 3", style = MaterialTypography.h3)
                Text(text = "Headline 4", style = MaterialTypography.h4)
                Text(text = "Headline 5", style = MaterialTypography.h5)
                Text(text = "Headline 6", style = MaterialTypography.h6)
                Text(text = "Subtitle 1", style = MaterialTypography.subtitle1)
                Text(text = "Subtitle 2", style = MaterialTypography.subtitle2)
                Text(text = "Body 1", style = MaterialTypography.body1)
                Text(text = "Body 2", style = MaterialTypography.body2)
                Text(text = "BUTTON", style = MaterialTypography.button)
                Text(text = "Caption", style = MaterialTypography.caption)
                Text(text = "OVERLINE", style = MaterialTypography.overline)
            }
        }
    }
}
