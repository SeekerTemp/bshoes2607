/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entity.ChatLieu;

/**
 *
 * @author dungcc
 */
public interface Dao_ChatLieu extends CrudDAO<ChatLieu, Integer>{
    ChatLieu findByName(String name);
}
