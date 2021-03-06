package eu.davidea.samples.flexibleadapter.models;

import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IExpandable;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.samples.flexibleadapter.R;
import eu.davidea.samples.flexibleadapter.models.ExpandableHeaderItem.ExpandableHeaderViewHolder;
import eu.davidea.viewholders.ExpandableViewHolder;

/**
 * This is an experiment to evaluate how a Section with header can also be expanded/collapsed.
 * <p>Here, it still benefits of the common fields declared in AbstractModelItem.</p>
 * It's important to note that, the ViewHolder must be specified in all &lt;diamond&gt; signature.
 */
public class ExpandableHeaderItem
		extends AbstractModelItem<ExpandableHeaderViewHolder>
		implements IExpandable<ExpandableHeaderViewHolder, SubItem>,
		IHeader<ExpandableHeaderViewHolder> {

	private static final long serialVersionUID = -1882711111814491060L;

	/* Flags for FlexibleAdapter */
	private boolean mExpanded = false;

	/* subItems list */
	private List<SubItem> mSubItems;


	public ExpandableHeaderItem(String id) {
		super(id);
		setDraggable(true);
		//We start with header shown and expanded
		setHidden(false);
		setExpanded(true);
		//NOT selectable (otherwise ActionMode will be activated on long click)!
		setSelectable(false);
	}

	@Override
	public boolean isExpanded() {
		return mExpanded;
	}

	@Override
	public void setExpanded(boolean expanded) {
		mExpanded = expanded;
	}

	@Override
	public int getExpansionLevel() {
		return 0;
	}

	@Override
	public List<SubItem> getSubItems() {
		return mSubItems;
	}

	public final boolean hasSubItems() {
		return mSubItems!= null && mSubItems.size() > 0;
	}

	public boolean removeSubItem(SubItem item) {
		return item != null && mSubItems.remove(item);
	}

	public boolean removeSubItem(int position) {
		if (mSubItems != null && position >= 0 && position < mSubItems.size()) {
			mSubItems.remove(position);
			return true;
		}
		return false;
	}

	public void addSubItem(SubItem subItem) {
		if (mSubItems == null)
			mSubItems = new ArrayList<SubItem>();
		mSubItems.add(subItem);
	}

	public void addSubItem(int position, SubItem subItem) {
		if (mSubItems != null && position >= 0 && position < mSubItems.size()) {
			mSubItems.add(position, subItem);
		} else
			addSubItem(subItem);
	}

	@Override
	public int getLayoutRes() {
		return R.layout.recycler_expandable_header_item;
	}

	@Override
	public ExpandableHeaderViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
		return new ExpandableHeaderViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
	}

	@Override
	public void bindViewHolder(FlexibleAdapter adapter, ExpandableHeaderViewHolder holder, int position, List payloads) {
		if (payloads.size() > 0) {
			Log.i(this.getClass().getSimpleName(), "ExpandableHeaderItem Payload " + payloads);
		} else {
			holder.mTitle.setText(getTitle());
		}
		setSubtitle(String.valueOf(adapter.getCurrentChildren(this).size()) +
				" subItems (" + (isExpanded() ? "expanded" : "collapsed") + ")");
		holder.mSubtitle.setText(getSubtitle());
	}

	/**
	 * Provide a reference to the views for each data item.
	 * Complex data labels may need more than one view per item, and
	 * you provide access to all the views for a data item in a view holder.
	 */
	static class ExpandableHeaderViewHolder extends ExpandableViewHolder {

		public TextView mTitle;
		public TextView mSubtitle;
		public ImageView mHandleView;

		public ExpandableHeaderViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter, true);//True for sticky
			mTitle = (TextView) view.findViewById(R.id.title);
			mSubtitle = (TextView) view.findViewById(R.id.subtitle);
			this.mHandleView = (ImageView) view.findViewById(R.id.row_handle);
			if (adapter.isHandleDragEnabled()) {
				this.mHandleView.setVisibility(View.VISIBLE);
				setDragHandleView(mHandleView);
			} else {
				this.mHandleView.setVisibility(View.GONE);
			}

			//Support for StaggeredGridLayoutManager
			if (itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
				((StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams()).setFullSpan(true);
			}
		}

		@Override
		protected boolean isViewExpandableOnClick() {
			return true;//true by default
		}

		@Override
		protected void expandView(int position) {
			super.expandView(position);
			//Let's notify the item has been expanded
			if (mAdapter.isExpanded(position)) mAdapter.notifyItemChanged(position, true);
		}

		@Override
		protected void collapseView(int position) {
			super.collapseView(position);
			//Let's notify the item has been collapsed
			if (!mAdapter.isExpanded(position)) mAdapter.notifyItemChanged(position, true);
		}

	}

	@Override
	public String toString() {
		return "ExpandableHeaderItem[" + super.toString() + "//SubItems" + mSubItems + "]";
	}

}