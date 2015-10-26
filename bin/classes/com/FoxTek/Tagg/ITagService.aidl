package com.FoxTek.Tagg;

import java.util.List;
/**
 * Created by ModdingFox on 10/31/13.
 */
interface ITagService {
boolean It_Status();
void Refresh_Servers();
List<String> Scan();
List<String> GetAllScan_Results();
boolean Tag(int selected_player);
void Tag_Request_Send(int selected_player);
}
