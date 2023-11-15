package nl.ndat.blackdroid

import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.graphics.drawable.ColorDrawable
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.graphics.set
import androidx.core.view.setPadding
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.RepeatMode
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(FlexboxLayout(this).apply {
			flexWrap = FlexWrap.WRAP

			background = ColorDrawable(android.graphics.Color.rgb(20, 20, 20))
			setPadding(100.dip)

			// Control
			addView(View(context).apply {
				layoutParams = LinearLayout.LayoutParams(100.dip, 100.dip)
				background = ColorDrawable(android.graphics.Color.rgb(1, 1, 1))
			})

			addSpacerView()

			// Plain view
			addView(View(context).apply {
				layoutParams = LinearLayout.LayoutParams(100.dip, 100.dip)
				background = ColorDrawable(android.graphics.Color.BLACK)
			})

			addSpacerView()

			// Surface view
			for (useHardwareCanvas in arrayOf(false, true)) {
				val colors = buildList {
					add(android.graphics.Color.BLACK)

					if (Build.VERSION.SDK_INT >= 26) add(android.graphics.Color.pack(0f, 0f, 0f))
					else add(android.graphics.Color.RED)
				}
				for (color in colors) {
					addView(SurfaceView(context).apply {
						layoutParams = LinearLayout.LayoutParams(100.dip, 100.dip)
						holder.addCallback(object : SurfaceHolder.Callback {
							override fun surfaceCreated(holder: SurfaceHolder) = draw(holder)
							override fun surfaceChanged(
								holder: SurfaceHolder,
								format: Int,
								width: Int,
								height: Int
							) =
								draw(holder)

							override fun surfaceDestroyed(holder: SurfaceHolder) = draw(holder)

							fun draw(holder: SurfaceHolder) {
								val canvas = when {
									useHardwareCanvas && Build.VERSION.SDK_INT >= 26 -> holder.lockHardwareCanvas()
									else -> holder.lockCanvas()
								}

								if (color is Int) canvas.drawColor(color)
								else if (color is Long && Build.VERSION.SDK_INT >= 29) canvas.drawColor(
									color
								)
								else canvas.drawColor(android.graphics.Color.RED)

								holder.unlockCanvasAndPost(canvas)
							}
						})
					})

					addSpacerView()
				}
			}

			// Canvas view
			addView(GLSurfaceView(context).apply {
				layoutParams = LinearLayout.LayoutParams(100.dip, 100.dip)
				setEGLContextClientVersion(2)
				setRenderer(object : GLSurfaceView.Renderer {
					override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) = draw(gl)
					override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) = draw(gl)
					override fun onDrawFrame(gl: GL10) = draw(gl)

					fun draw(gl: GL10) {
						gl.glClearColor(0f, 0f, 0f, 1f)
						gl.glClear(GL10.GL_DEPTH_BUFFER_BIT or GL10.GL_COLOR_BUFFER_BIT)
					}
				})
			})

			addSpacerView()

			// Black HDR bitmap view
			if (Build.VERSION.SDK_INT >= 29) {
				addView(ImageView(context).apply {
					layoutParams = LinearLayout.LayoutParams(100.dip, 100.dip)
					val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGBA_F16, false)
					bitmap[0, 0] = android.graphics.Color.BLACK
					bitmap.setColorSpace(ColorSpace.get(ColorSpace.Named.EXTENDED_SRGB))
					setImageBitmap(bitmap)
				})
			} else {
				addView(View(context).apply {
					layoutParams = LinearLayout.LayoutParams(100.dip, 100.dip)
					background = ColorDrawable(android.graphics.Color.RED)
				})
			}

			addSpacerView()

			// Plain compose box
			addView(ComposeView(context).apply {
				setContent {
					Box(
						modifier = Modifier
							.background(Color.Black)
							.size(100.dp, 100.dp)
					) {}
				}
			})

			addSpacerView()

			// ExoPlayer HDR video
			addView(PlayerView(context).apply {
				layoutParams = LinearLayout.LayoutParams(100.dip, 100.dip)

				player = ExoPlayer.Builder(context).build().apply {
					repeatMode = REPEAT_MODE_ALL

					volume = 0f

					setMediaItem(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.sample_video)))
					prepare()
					play()
				}
			})
		})
	}

	private fun ViewGroup.addSpacerView() = addView(View(context).apply {
		layoutParams = LinearLayout.LayoutParams(10.dip, 10.dip)
	})

	private val Int.dip
		get() = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			toFloat(),
			resources.displayMetrics
		).toInt()
}
