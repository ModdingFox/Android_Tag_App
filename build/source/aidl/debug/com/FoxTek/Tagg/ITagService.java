/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\moddi_000\\Desktop\\Tag\\src\\com\\FoxTek\\Tagg\\ITagService.aidl
 */
package com.FoxTek.Tagg;
/**
 * Created by ModdingFox on 10/31/13.
 */
public interface ITagService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.FoxTek.Tagg.ITagService
{
private static final java.lang.String DESCRIPTOR = "com.FoxTek.Tagg.ITagService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.FoxTek.Tagg.ITagService interface,
 * generating a proxy if needed.
 */
public static com.FoxTek.Tagg.ITagService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.FoxTek.Tagg.ITagService))) {
return ((com.FoxTek.Tagg.ITagService)iin);
}
return new com.FoxTek.Tagg.ITagService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_It_Status:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.It_Status();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_Refresh_Servers:
{
data.enforceInterface(DESCRIPTOR);
this.Refresh_Servers();
reply.writeNoException();
return true;
}
case TRANSACTION_Scan:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _result = this.Scan();
reply.writeNoException();
reply.writeStringList(_result);
return true;
}
case TRANSACTION_Tag:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.Tag(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.FoxTek.Tagg.ITagService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public boolean It_Status() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_It_Status, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void Refresh_Servers() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_Refresh_Servers, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.util.List<java.lang.String> Scan() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<java.lang.String> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_Scan, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArrayList();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean Tag(int selected_player) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(selected_player);
mRemote.transact(Stub.TRANSACTION_Tag, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_It_Status = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_Refresh_Servers = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_Scan = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_Tag = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public boolean It_Status() throws android.os.RemoteException;
public void Refresh_Servers() throws android.os.RemoteException;
public java.util.List<java.lang.String> Scan() throws android.os.RemoteException;
public boolean Tag(int selected_player) throws android.os.RemoteException;
}
