package net.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.basicmodel.R
import net.entity.PDFEntity

class PDFAdapter(layoutResId: Int, data: ArrayList<PDFEntity>?) :
    BaseQuickAdapter<PDFEntity, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: PDFEntity) {
        holder.setText(R.id.name, item.name)
            .setText(R.id.path, item.path)
    }
}