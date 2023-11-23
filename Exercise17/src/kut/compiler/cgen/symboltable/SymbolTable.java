package kut.compiler.cgen.symboltable;

import java.util.HashMap;
import java.util.Vector;

import kut.compiler.cgen.type.Type;
import kut.compiler.error.SemanticErrorException;
import kut.compiler.tokenizer.Token;

public class SymbolTable {
	
	/**
	 * 
	 */
	public HashMap<String, String> 	mapGlobalVariableToMemoryAddressLabel;
	public HashMap<String, Token>	mapGlobalVariableToFirstDeclaration;
	public HashMap<String, Type>	mapGlobalVariableToType;

	/**
	 * 
	 */
	public SymbolTable() {
		this.resetGlobalVariablesDeclaration();
	}

	/**
	 * 
	 */
	public void resetGlobalVariablesDeclaration() {
		mapGlobalVariableToMemoryAddressLabel = new HashMap<String, String>();
		mapGlobalVariableToType = new HashMap<String, Type>();
	}
	
	
	/**
	 * @param id
	 * @return
	 */
	public String getMemorryAddressLabelOfGlobalVariable(String id) {
		if (mapGlobalVariableToMemoryAddressLabel.containsKey(id) == false) {
			return null;
		}
		return mapGlobalVariableToMemoryAddressLabel.get(id);
	}
	
	/**
	 * @param id
	 */
	public void declareGlobalVariable(String id, Type type) throws SemanticErrorException {
		//既にグローバル変数として宣言されていたら、以前のタイプとマッチしているか調べる。
		if (mapGlobalVariableToType.containsKey(id)){
			Type existing = mapGlobalVariableToType.get(id);
			
			//同じだったら無視
			if (type.equals(existing)){
				return;				
			}
			//違ったら例外
			throw new SemanticErrorException("the variable: " + id 
					+ " is declared again but with a different type " 
					+ "(first declaration: " + existing.getTypeNameString()
					+ ", this declaration: " + type.getTypeNameString() + ".");
		}
		
		//初めての宣言であれば、型をmapに保存。またラベルも生成しmapに保存。
		mapGlobalVariableToType.put(id, type);
		
		String lbl = this.generateGlobalVariableMemoryAddressLabel(id);
		mapGlobalVariableToMemoryAddressLabel.put(id, lbl);
		
	
	}
	
	/**
	 * @param id
	 * @return
	 */
	private String generateGlobalVariableMemoryAddressLabel(String id) {
		return "global_variable#" + id;
	}
	
	
	/**
	 * @return
	 */
	public Vector<String> getAllMemoryAddressLabels(){
		Vector<String> labels = new Vector<String>();
		
		labels.addAll(mapGlobalVariableToMemoryAddressLabel.values());
		return labels;
	}
	
	/**
	 * 
	 */
	public TypeOfId getTypeOfId(String id) {
		if (mapGlobalVariableToMemoryAddressLabel.containsKey(id)) {
			return TypeOfId.GlobalVariable;
		}
		
		return TypeOfId.UNKNOWN;
	}
	
	/**
	 * @param gvname
	 * @return
	 */
	public Type getTypeOfGlobalVariable(String gvname) {
		if (mapGlobalVariableToType.containsKey(gvname) == false){
			throw new RuntimeException("a bug. the code shouldn't reach here.");
		}
		return mapGlobalVariableToType.get(gvname);
	}
	
}
