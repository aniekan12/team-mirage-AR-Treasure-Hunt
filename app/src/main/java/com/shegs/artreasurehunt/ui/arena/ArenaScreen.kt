package com.shegs.artreasurehunt.ui.arena

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shegs.artreasurehunt.R
import com.shegs.artreasurehunt.data.models.ArenaModel
import com.shegs.artreasurehunt.data.network.request_and_response_models.Resource
import com.shegs.artreasurehunt.navigation.NestedNavItem
import com.shegs.artreasurehunt.viewmodels.ArenaViewModel
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonHighlightAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaScreen(
    arenaViewModel: ArenaViewModel,
    navController: NavController
) {

    // Collect the list of arenas from the ArenaViewModel
    val arenas: List<ArenaModel> by arenaViewModel.arenas.collectAsState(emptyList())

    var isCreateDialogVisible by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { isCreateDialogVisible = true },
                content = { Icon(imageVector = Icons.Default.Add, contentDescription = "") }
            )
        }
    ) {
        ArenaListScreen(arenas, arenaViewModel, navController)
    }

    if (isCreateDialogVisible) {
        CreateArenaDialog(
            viewModel = arenaViewModel,
            onDismiss = { isCreateDialogVisible = false }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ArenaListScreen(
    arenas: List<ArenaModel>,
    viewModel: ArenaViewModel,
    navController: NavController
) {
    val arenasResource by viewModel.arenasFlow.collectAsState()

    when (arenasResource) {
        is Resource.Loading -> {
            // Show a loading indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                )
            }
        }

        is Resource.Success -> {
            val arenas = (arenasResource as Resource.Success<List<ArenaModel>>).data

            if (arenas.isNullOrEmpty()) {
                // Show a message when no arenas are available
                Text("No arenas available.")
            } else {
                LazyColumn {
                    items(arenas) { arena ->
                        ArenaItem(arena, viewModel, navController)
                    }
                }
            }
        }

        is Resource.Error -> {
            // Handle the error case
            Text("Error: ${(arenasResource as Resource.Error).message}")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchArenas()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ArenaItem(
    arena: ArenaModel,
    arenaViewModel: ArenaViewModel,
    navController: NavController
) {

    val builder = rememberBalloonBuilder {
        setArrowSize(10)
        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        setArrowPosition(0.5f)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setPadding(12)
        setCornerRadius(8f)
        setBackgroundColorResource(R.color.teal_700)
        setBalloonAnimation(BalloonAnimation.ELASTIC)
        setIsVisibleOverlay(true)
        setOverlayColorResource(R.color.transparent_black)
        setBalloonHighlightAnimation(BalloonHighlightAnimation.HEARTBEAT)
        setDismissWhenClicked(true)
    }

    Balloon(
        modifier = Modifier,
        builder = builder,
        balloonContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {

                Text(
                    text = "Arena Name:",
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    lineHeight = 16.sp
                )

                Text(
                    text = "${arena.arenaName}",
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Description:",
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                )

                Text(
                    text = "${arena.arenaDesc}",
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Location:",
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    lineHeight = 16.sp
                )

                Text(
                    text = "${arena.arenaLocation}",
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier,
                    onClick = { navController.navigate(NestedNavItem.GameScreen.route) },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
                ) {
                    Text(
                        text = "Play in this Arena",
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_medium))
                    )
                }

            }

        }
    ) { balloonWindow ->
        Box(
            modifier = Modifier
                .padding(24.dp)
                .clickable {
                    balloonWindow.showAlignBottom()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = arena.imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Text(
                    text = arena.arenaName,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(8.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight(700),
                    fontFamily = FontFamily(Font(R.font.rye_regular)),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BottomSheet(onDismiss: () -> Unit) {
//    val modalBottomSheetState = rememberModalBottomSheetState()
//
//    ModalBottomSheet(
//        onDismissRequest = { onDismiss() },
//        sheetState = modalBottomSheetState,
//        dragHandle = { BottomSheetDefaults.DragHandle() },
//    ) {
//        BottomSheetContent()
//    }
//}
//
//
//@Composable
//fun BottomSheetContent(
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier
//            .padding(16.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        // Delete icon and text
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Delete,
//                contentDescription = null,
//                tint = Color.Red,
//                modifier = Modifier.clickable {  }
//            )
//            Text(
//                text = "Delete",
//                color = Color.Red,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.clickable {  }
//            )
//        }
//
//        // Share icon and text
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Share,
//                contentDescription = null,
//                modifier = Modifier.clickable {  }
//            )
//            Text(
//                text = "Share",
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.clickable {  }
//            )
//        }
//    }
//}