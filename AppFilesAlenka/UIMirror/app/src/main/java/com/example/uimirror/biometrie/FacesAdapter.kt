package com.example.uimirror.biometrie

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.opencv.android.Utils.matToBitmap
import org.opencv.core.Mat

class FacesAdapter(
    private val faces: List<Mat>,
    private val onFaceClick: (Int) -> Unit
) : RecyclerView.Adapter<FacesAdapter.FaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaceViewHolder {
        val imageView = ImageView(parent.context)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return FaceViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: FaceViewHolder, position: Int) {
        val mat = faces[position]
        val bitmap = android.graphics.Bitmap.createBitmap(
            mat.cols(), mat.rows(), android.graphics.Bitmap.Config.ARGB_8888
        )
        matToBitmap(mat, bitmap) // Convert Mat to Bitmap
        holder.imageView.setImageBitmap(bitmap)

        holder.imageView.setOnClickListener {
            onFaceClick(position)  // This invokes the onFaceSelected callback in AddUserActivity
        }
    }

    override fun getItemCount() = faces.size

    class FaceViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
