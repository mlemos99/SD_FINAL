/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd_lei;

import java.util.ArrayList;

/**
 *
 * @author Utilizador
 */
public class ArrayCidadao<T> {
    private ArrayList<T> cidadao = new ArrayList<>();

    public synchronized void addCidadao(T c) {
        cidadao.add(c);
        
    }

    public ArrayList<T> getCidadao() {
        return cidadao;
    }
    
    
    
    
}
