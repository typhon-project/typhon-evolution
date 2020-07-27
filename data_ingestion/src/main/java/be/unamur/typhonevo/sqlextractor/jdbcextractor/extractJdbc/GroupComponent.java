package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.io.Serializable;

public class GroupComponent  implements Comparable<GroupComponent>, Serializable{
	int pos;
	Column comp;
	
	GroupComponent(int pos, Column comp){
		this.pos = pos;
		this.comp = comp;
	}

	public int getPos() {
		return pos;
	}

	public Column getComponent() {
		return(comp);
	}

	public int compareTo(GroupComponent o) {
    	int oPos = o.getPos();
    	if(pos < oPos){
    		return(-1);
    	} else if (pos == oPos){
    		return(0);
    	} else {
    		return(1);
    	}
	}
}
