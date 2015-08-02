package com.submarine.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;

public class SaveManager {

	private FileHandle savefile;
	private Save save;
	
	public SaveManager() {
		savefile = Gdx.files.local("bin/save.json");	// Open filehandle from local because internal is readonly.
														// Folder is /bin/ because that's the folder code gets compiled
		save = getSave();
	}

	public static class Save {
		public ObjectMap<String, Object> data = new ObjectMap<String, Object>();
	}

	private Save getSave() {
		Save save = new Save();
		if (savefile.exists()) {
			Json json = new Json();
			save = json.fromJson(Save.class, savefile.readString());
		} 
		return save;
	}

	public void saveToJson() {
		Json json = new Json();
		json.setOutputType(OutputType.json);
		System.out.println(json.prettyPrint(save));
		savefile.writeString(json.prettyPrint(save), false);
	}
	
    @SuppressWarnings("unchecked")
	public <T> T loadDataValue(String key, Class type){
        if(save.data.containsKey(key)) {
        	return (T) save.data.get(key);
        } else {
        	return null;
        }
    }
	
    public void saveDataValue(String key, Object object){
        save.data.put(key, object);
        saveToJson();
    }
    
    public ObjectMap<String, Object> getAllData(){
        return save.data;
    }
    
}
