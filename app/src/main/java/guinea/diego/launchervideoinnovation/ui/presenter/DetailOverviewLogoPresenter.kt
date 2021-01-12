package guinea.diego.launchervideoinnovation.ui.presenter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.view.View
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.Presenter
import guinea.diego.launchervideoinnovation.R

class DetailOverviewLogoPresenter: DetailsOverviewLogoPresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val imageView = LayoutInflater.from(parent.context)
                .inflate(R.layout.lb_fullwidth_details_overview_logo, parent, false) as ImageView
        val width = parent.resources.getDimensionPixelSize(R.dimen.detail_thumb_width)
        val height = parent.resources.getDimensionPixelSize(R.dimen.detail_thumb_height)
        imageView.layoutParams = ViewGroup.MarginLayoutParams(width, height)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val row = item as DetailsOverviewRow
        val imageView = viewHolder.view as ImageView
        imageView.setImageDrawable(row.imageDrawable)
        if (isBoundToImage(viewHolder as ViewHolder, row)) {
            viewHolder.parentPresenter.notifyOnBindLogo(viewHolder.parentViewHolder)
        }
    }

    internal class ViewHolder(view: View): DetailsOverviewLogoPresenter.ViewHolder(view) {
        override fun getParentPresenter(): FullWidthDetailsOverviewRowPresenter {
            return mParentPresenter
        }

        override fun getParentViewHolder(): FullWidthDetailsOverviewRowPresenter.ViewHolder {
            return mParentViewHolder
        }
    }
}