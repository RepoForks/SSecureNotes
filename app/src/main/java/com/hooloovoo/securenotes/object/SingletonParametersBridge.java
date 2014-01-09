package com.hooloovoo.securenotes.object;

import java.util.HashMap;

/**
 * Created by angelo on 31/12/13.
 */
public class SingletonParametersBridge {

    private static SingletonParametersBridge instance = null;
    private HashMap<String, Object> map;

    /**
     * metodo che restituisce l'istanza dalla quale prende i dati.
     * Eventalmente non fosse allocata la crea.
     * @return
     */
    public static SingletonParametersBridge getInstance() {
        if (instance == null)
            instance = new SingletonParametersBridge();
        return instance;
    }

    /**
     * metodo che aggiunge il parametro param alla chiave key
     * @param key dell'oggetto
     * @param value valore dell'oggetto
     */
    public void addParameter(String key, Object value) {
        map.put(key, value);
    }

    /**
     * metodo che restituisce l'oggetto in base alla ricerca per
     * chiave con la key
     * @param key chiave da ricercare
     * @return oggetto che corrisponde alla chiave key
     */
    public Object getParameter(String key) {
        return map.get(key);
    }

    /**
     * metodo che rimuove l'oggetto che presenta la chiave key
     * @param key chiave dell'oggetto da eliminare
     */
    public void removeParameter(String key) {
        map.remove(key);
    }

    /**
     * costruttore privato
     */
    private SingletonParametersBridge() {
        map = new HashMap<String, Object>();
    }
}
