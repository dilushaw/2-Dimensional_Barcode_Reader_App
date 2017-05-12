package lk.dialog.corporate.Qr.utils;

import org.extremecomponents.table.cell.FilterOption;

class MyFilterClass implements FilterOption,Comparable<MyFilterClass> {
	String label;
	String value;

	public MyFilterClass(String label, String value) {
		super();
		this.label = label;
		this.value = value;
	}

	public MyFilterClass(String label, int value) {
		super();
		this.label = label;
		this.value = String.valueOf(value);
	}

	public Object getLabel() {
		// TODO Auto-generated method stub
		return label;
	}

	public Object getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public int compareTo(MyFilterClass o) {
		if(label!=null && o!=null){
		return this.label.compareTo(o.label);
		}else{
		return 0;
		}
	}

}