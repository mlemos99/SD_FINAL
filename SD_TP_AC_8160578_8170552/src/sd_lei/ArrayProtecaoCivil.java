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
public class ArrayProtecaoCivil<T> {
    private ArrayList<T> PC = new ArrayList<>();

    public synchronized void addPC(T c) {
        PC.add(c);
        
    }

    public ArrayList<T> getPC() {
        return PC;
    }
}
