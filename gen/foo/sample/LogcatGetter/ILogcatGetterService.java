/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\mori\\dev\\workspace\\LogcatGetter\\src\\foo\\sample\\LogcatGetter\\ILogcatGetterService.aidl
 */
package foo.sample.LogcatGetter;
public interface ILogcatGetterService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements foo.sample.LogcatGetter.ILogcatGetterService
{
private static final java.lang.String DESCRIPTOR = "foo.sample.LogcatGetter.ILogcatGetterService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an foo.sample.LogcatGetter.ILogcatGetterService interface,
 * generating a proxy if needed.
 */
public static foo.sample.LogcatGetter.ILogcatGetterService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof foo.sample.LogcatGetter.ILogcatGetterService))) {
return ((foo.sample.LogcatGetter.ILogcatGetterService)iin);
}
return new foo.sample.LogcatGetter.ILogcatGetterService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
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
case TRANSACTION_getDispData:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _result = this.getDispData();
reply.writeNoException();
reply.writeStringList(_result);
return true;
}
case TRANSACTION_saveLog:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.saveLog(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stopWrite:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.stopWrite();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isWritting:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isWritting();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setLogBreak:
{
data.enforceInterface(DESCRIPTOR);
this.setLogBreak();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements foo.sample.LogcatGetter.ILogcatGetterService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.util.List<java.lang.String> getDispData() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<java.lang.String> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDispData, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArrayList();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int saveLog(java.lang.String FileName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(FileName);
mRemote.transact(Stub.TRANSACTION_saveLog, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int stopWrite() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopWrite, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isWritting() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isWritting, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLogBreak() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setLogBreak, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getDispData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_saveLog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopWrite = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_isWritting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setLogBreak = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public java.util.List<java.lang.String> getDispData() throws android.os.RemoteException;
public int saveLog(java.lang.String FileName) throws android.os.RemoteException;
public int stopWrite() throws android.os.RemoteException;
public boolean isWritting() throws android.os.RemoteException;
public void setLogBreak() throws android.os.RemoteException;
}
