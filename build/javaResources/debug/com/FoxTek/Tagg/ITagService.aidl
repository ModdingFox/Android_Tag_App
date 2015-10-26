package com.FoxTek.Tagg;

import java.util.List;
/**
 * Created by ModdingFox on 10/31/13.
 */
interface ITagService {
boolean It_Status();
void Refresh_Servers();
List<String> Scan();
boolean Tag(int selected_player);
}
