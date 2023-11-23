package kut.compiler.cgen.type;

import kut.compiler.error.SemanticErrorException;

class TypeID {
	public static final int UNKNOWN 	= -1;
	public static final int VOID		= 1	;
	public static final int INT 		= 2	;
	public static final int BOOLEAN 	= 3	;	
}
public class Type {
	
	private int	typeID = 0;
	
	public static final Type UNKNOWN	= new Type(TypeID.UNKNOWN);
	public static final Type VOID		= new Type(TypeID.VOID);
	public static final Type INT 		= new Type(TypeID.INT);
	public static final Type BOOLEAN 	= new Type(TypeID.BOOLEAN);
	
	/**
	 * @param typename
	 * @return
	 */
	public static Type convertTypeNameToType(String tname) throws SemanticErrorException {
		
		if ("int".equals(tname)) {
			return Type.INT;
		}
		
		if ("boolean".equals(tname)) {
			return Type.BOOLEAN;
		}
		
		throw new SemanticErrorException("unknown typename: " + tname);
	}
	
	/**
	 * @param t
	 * @return
	 */
	public boolean equals(Type t) {
		return this.getTypeID() == t.getTypeID();
	}
	
	/**
	 * @param type
	 */
	public Type(int typeID) {
		this.typeID = typeID;
	}
	
	/**
	 * @return
	 */
	public int getTypeID() {
		return typeID;
	}
	
	/**
	 * @return
	 */
	public String getTypeNameString() {
		
		if (this.typeID == TypeID.INT) {
			return "int";
		}
		
		if (this.typeID == TypeID.BOOLEAN) {
			return "boolean";
		}
		
		throw new RuntimeException("a bug. the code shouldn't reach here");
	}
}
