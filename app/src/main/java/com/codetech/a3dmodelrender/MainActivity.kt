package com.codetech.a3dmodelrender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codetech.a3dmodelrender.ui.theme._3dModelRenderTheme
import io.github.sceneview.Scene
import io.github.sceneview.animation.Transition.animateRotation
import io.github.sceneview.environment.Environment
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberView
import java.io.IOException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _3dModelRenderTheme {
                val modelFiles = remember { mutableStateListOf<String>() }
                var selectedModel by remember { mutableStateOf("four.glb") }
                var currentModelNode by remember { mutableStateOf<ModelNode?>(null) }

                //get all models from assets folder that ends with .glb
                LaunchedEffect(Unit) {
                    try {
                        assets.list("")?.filter { it.endsWith(".glb") }?.let {
                            modelFiles.addAll(it)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                //app title
                                Text(
                                    text = stringResource(id = R.string.app_name)
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    },
                    //bottom lazy row
                    bottomBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(modelFiles) { file ->
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (file == selectedModel) Color.LightGray else Color.White,
                                                shape = MaterialTheme.shapes.small
                                            )
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                            .clickable {
                                                selectedModel = file
                                            }
                                    ) {
                                        Text(text = file)
                                    }
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    //box of lazy row
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(Color.White)
                    ) {
                        val engine = rememberEngine()
                        val modelLoader = rememberModelLoader(engine)
                        val cameraNode = rememberCameraNode(engine).apply {
                            position = Position(z = 4.0f)
                        }
                        val centerNode = rememberNode(engine)
                            .addChildNode(cameraNode)
                        val cameraTransition = rememberInfiniteTransition(label = "CameraTransition")
                        val cameraRotation by cameraTransition.animateRotation(
                            initialValue = Rotation(y = 0.0f),
                            targetValue = Rotation(y = 360.0f),
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 7.seconds.toInt(DurationUnit.MILLISECONDS))
                            )
                        )

                        // Create and add the initial model to centerNode
                        LaunchedEffect(selectedModel) {
                            // Remove previous model
                            currentModelNode?.let {
                                centerNode.removeChildNode(it)
                            }

                            // Add new model
                            val newModel = ModelNode(
                                modelInstance = modelLoader.createModelInstance(
                                    assetFileLocation = selectedModel
                                ),
                                scaleToUnits = 1.0f
                            )
                            centerNode.addChildNode(newModel)
                            currentModelNode = newModel
                        }
                        //scene view that show 3d model
                        Scene(modifier = Modifier.fillMaxSize().background(Color.White),
                            engine = engine,
                            modelLoader = modelLoader,
                            cameraNode = cameraNode,
                            childNodes = listOf(centerNode),
                            isOpaque = true,
                            onFrame = {
                                centerNode.rotation = cameraRotation
                                cameraNode.lookAt(centerNode)
                            }
                        )
                    }
                }
            }
        }
    }
}
