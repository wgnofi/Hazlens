package com.example.hazlens.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.hazlens.R
import com.example.hazlens.data.getFont
import com.example.hazlens.ui.theme.HazlensTheme
import com.example.hazlens.ui.theme.bitmapDecayFont
import com.example.hazlens.ui.theme.hazlensFont


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazlensScreen(hazViewModel: HazViewModel = viewModel(factory = HazViewModel.Factory)) {

    val uiState by hazViewModel.hzUiState.collectAsStateWithLifecycle()
    val options = hazViewModel.filterType
    val context = LocalContext.current

    var selected by remember {
        mutableIntStateOf(0)
    }
    var selectedImageUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> selectedImageUri = uri}
    )
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Hazlens", fontWeight = FontWeight.Bold, fontSize = 32.sp, fontFamily = hazlensFont)
            })
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(color = Color.LightGray, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .size(width = 350.dp, height = 350.dp).clickable(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ),
                shadowElevation = 5.dp, shape = RoundedCornerShape(20.dp)
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(align = Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.upload),
                            contentDescription = null,
                            modifier = Modifier.sizeIn(minWidth = 44.dp, minHeight = 44.dp),
                            tint = Color.DarkGray
                        )
                        Text("Upload Image")
                    }
                }

            }
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text("Select your filter", fontWeight = FontWeight.SemiBold, fontSize = 24.sp, fontFamily = bitmapDecayFont)
                Spacer(modifier = Modifier.padding(16.dp))
                options.forEach {
                    FilterOption(
                        name = stringResource(it.filterTypeRes),
                        selected = selected == it.filterType,
                        font = it.getFont(it.filterType),
                        onSelected = {
                            selected = it.filterType
                        }
                    )
                }
            }

            FilterActions(
                hazUiState = uiState,
                onStartClick = {hazViewModel.applyFilter(selected,
                    selectedImageUri.toString())},
                onCancelClick = hazViewModel::cancelWork,
                onSeeFileClick = {
                    currentUri ->
                    showProcessedImage(context, currentUri)
                },
                enabled = selectedImageUri != null
            )
        }

    }
}
@Composable
fun FilterActions(
    hazUiState: HazUiState,
    onStartClick: () -> Unit,
    onCancelClick:() -> Unit,
    onSeeFileClick:(String) -> Unit,
    enabled: Boolean
) {
    when (hazUiState) {
       is HazUiState.Default -> {
           ElevatedButton(onClick = onStartClick,
               shape = RoundedCornerShape(10.dp),
               enabled = enabled,
               modifier = Modifier.fillMaxWidth().wrapContentWidth(align = Alignment.CenterHorizontally)
                   .padding(top = 16.dp)
           ) {
               Text("Process The Image")
           }
       }
        is HazUiState.Loading -> {
            FilledTonalButton(onCancelClick) { Text("Cancel") }
            Text("Loading...", fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth().wrapContentWidth(align = Alignment.CenterHorizontally))
        }
        is HazUiState.Complete -> {
            ElevatedButton(onClick = onStartClick,
                shape = RoundedCornerShape(10.dp),
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().wrapContentWidth(align = Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            ) {
                Text("Process The Image")
            }
            Spacer(modifier = Modifier.width(20.dp))
            FilledTonalButton(onClick = {onSeeFileClick(hazUiState.outputUri)}, shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text("Show The Result")
            }
        }
    }
}
private fun showProcessedImage(context: Context, currentUri: String) {
    val uri = if (currentUri.isNotEmpty()) {
        currentUri.toUri()
    } else {
        null
    }
    val actionView = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(actionView)
}
@Composable
fun FilterOption(
                  name: String,
                    selected: Boolean,
                  onSelected: () -> Unit,
                  font: FontFamily) {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        RadioButton(selected = selected, onClick = onSelected)
        Text(name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
@Preview
@Composable
private fun HLPreview() {
    HazlensTheme {
        HazlensScreen(
//            hazViewModel = viewModel()
        )
    }
}