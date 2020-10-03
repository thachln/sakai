/**
 * 
 */
package mksgroup.konva2d.chart.controller.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;

import mksgroup.konva2d.chart.model.ItemKine;

/**
 * @author lengocthach
 *
 */
public class MotionRuleEditor extends CustomCollectionEditor {
    public MotionRuleEditor(Class<? extends Collection> collectionType) {
        super(collectionType);
    }


    /* (non-Javadoc)
     * @see org.springframework.beans.propertyeditors.CustomCollectionEditor#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object value) {
        Object data = convertElement(value);

        if (data == null) {
            super.setValue(null);
        } else if (data instanceof List<?>){
            super.setValue((List<ItemKine>) convertElement(value));
        } else {
            super.setValue(value);
        }
    }

    
    /* (non-Javadoc)
     * @see org.springframework.beans.propertyeditors.CustomCollectionEditor#convertElement(java.lang.Object)
     */
    @Override
    protected Object convertElement(Object element) {
        if (element == null) {
            return null;
        } else {
            // continue;
        }
        
        String strItemKines = element.toString();
        try {
            List<ItemKine> listItemKine = new ArrayList<ItemKine>();
            JsonParser jsonParser = new JsonParser();

            JsonArray orderNoteArr = jsonParser.parse(strItemKines).getAsJsonArray();            

            
            listItemKine = getItemKines(orderNoteArr);
            return listItemKine;
        } catch (Exception e) {
            throw new ConversionFailedException(TypeDescriptor.valueOf(String.class),
                    TypeDescriptor.valueOf(ItemKine.class), strItemKines, null);
        }
    }

    /**
     * Lấy danh sách Quy luật chuyển động.
     * @param jsonKine
     * @return
     */
    public static List<ItemKine> getItemKines(JsonArray jsonKine) {
        int size = jsonKine.size() - 1;
        List<ItemKine> itemKines = new ArrayList<ItemKine>();
        ItemKine itemKine;
        JsonArray itemKineArr;

        for (int i = 0; i < size; i++) {
            itemKineArr = (JsonArray) jsonKine.get(i);
            itemKine = getItemKine(itemKineArr);

            if (itemKine != null) {
                itemKines.add(itemKine);
            } else {
                // Do nothing
            }
        }
        
        return itemKines;
    }

    /**
     * Lấy một phần tử của Quy luật chuyển động.
     * @param jsonItemKine
     * @return
     */
    public static ItemKine getItemKine(JsonArray jsonItemKine) {
        ItemKine itemKine;

        if (jsonItemKine == null) {
            return null;
        } else {
            // Continues
        }
        JsonElement jsonElement = jsonItemKine.get(0);
       
        Integer phaseEngAngle = ((jsonElement instanceof JsonNull) || (jsonElement.getAsString().isEmpty()) ) ? null : jsonElement.getAsInt();
        
        jsonElement = jsonItemKine.get(1);
        
        Integer height = ((jsonElement instanceof JsonNull) || (jsonElement.getAsString().isEmpty())) ? null : jsonElement.getAsInt();

        if ((phaseEngAngle != null) && (height != null)) {
            itemKine = new ItemKine(phaseEngAngle, height);

        } else {
            itemKine = null;
        }

        return itemKine;
    }
}
