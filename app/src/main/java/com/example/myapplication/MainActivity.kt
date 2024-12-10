package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val items = remember { mutableStateListOf("Item 1", "Item 2", "Item 3", "Item 4") }
                SwipeToDismissScreen(items = items)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen() {
    val context = LocalContext.current
    var showArabic by remember { mutableStateOf(true) }
    var showEnglish by remember { mutableStateOf(false) }

    // Load texts in both languages
    val arabicText = getLocalizedText(context, R.string.card_content, Locale("ar"))
    val englishText = getLocalizedText(context, R.string.card_content, Locale("en"))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Language Selection") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Checkboxes to toggle language display
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = showArabic,
                    onCheckedChange = { showArabic = it }
                )
                Text(text = "Show Arabic")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = showEnglish,
                    onCheckedChange = { showEnglish = it }
                )
                Text(text = "Show English")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display text based on checkbox selections
            if (showArabic) {
                Text(text = arabicText, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            }
            if (showEnglish) {
                Text(text = englishText, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSelectionScreenPreview() {
    MyApplicationTheme {
        LanguageSelectionScreen()
    }
}