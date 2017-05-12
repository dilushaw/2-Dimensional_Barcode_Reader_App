package lk.dialog.corporate.Qr.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.cell.FilterDroplistCell;
import org.extremecomponents.table.core.TableModel;



public class StatusDropListCell extends FilterDroplistCell {

    @Override
	protected List getFilterDropList(TableModel model, Column column) {
		List droplist = new ArrayList();

		// for(CustomerStatus customerStatus : CustomerStatus.values()){
		MyFilterClass myFilterClass = new MyFilterClass("Active", 1);
		MyFilterClass myFilterClass2 = new MyFilterClass("Inactive", 0);
            //    MyFilterClass myFilterClass3 = new MyFilterClass("All", -1);

		droplist.add(myFilterClass);
		droplist.add(myFilterClass2);
		// }
		Collections.sort(droplist);
		return droplist;

	}
}