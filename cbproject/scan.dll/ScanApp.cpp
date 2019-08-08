#include <iostream.h>
#include <windows.h>
#include <windef.h>
#include <stdlib.h>
#include <math.h>
#include "C:\Program Files\Precise Biometrics\Precise 100 SDK\include\pb_sdk.h"
#include "C:\jbproject\myapp\src\myapp_JPanel1.h"

typedef PBRETURN( WINAPI * Function1 ) ( void );
typedef PBRETURN( WINAPI * Function2 ) ( OUT void * );
typedef PBRETURN( WINAPI * Function3 ) ( IN void *, OUT BOOL * );

JNIEXPORT void JNICALL Java_myapp_JPanel1_scanapp( JNIEnv * a, jobject b, jobjectArray c, jintArray d )
{
  BOOL freeResult, runTimeLinkSuccess = FALSE;
  HINSTANCE dllHandle = NULL;
  Function1 Fn1 = NULL, Fn3 = NULL;
  Function2 Fn2 = NULL;
  Function3 Fn4 = NULL;

  //Load the dll and keep the handle to it
  dllHandle = LoadLibrary( "pb.dll" );

  // If the handle is valid, try to get the function address.
  if ( NULL != dllHandle )
  {
    //Get pointer to our function using GetProcAddress:
    Fn1 = ( Function1 )GetProcAddress( dllHandle, "pbInitialize" );
    Fn2 = ( Function2 )GetProcAddress( dllHandle, "pbGetRawImage" );
    Fn3 = ( Function1 )GetProcAddress( dllHandle, "pbClose" );
    Fn4 = ( Function3 )GetProcAddress( dllHandle, "pbFingerPresent" );
    // If the function address is valid, call the function.
    if ( runTimeLinkSuccess = ( ( NULL != Fn1 ) && ( NULL != Fn2 ) && ( NULL != Fn3 ) && ( NULL != Fn4 ) ) )
    {
      void * image1 = malloc( 90000 );
      BOOL present;

      Fn1();
      present = 0;
      Fn2( image1 );
      Fn4( image1, & present );
      Fn3();

      jboolean isCopy;
      jint * javaint = ( * a ).GetIntArrayElements( d, & isCopy );
      * javaint = present;


      for ( int i = 0; i < 300; i++ )
      {
        jintArray javaintArray = ( jintArray )( ( * a ).GetObjectArrayElement( c, i / 2 ) );
        jint * javaint1 = ( * a ).GetIntArrayElements( javaintArray, & isCopy );

        for ( int j = 0; j < 300; j++ )
        {
          * ( javaint1 + j / 2 ) = ( jint )( * ( ( byte * ) image1 + ( j * 300 ) + ( i ) ) );
        }

        ( * a ).ReleaseIntArrayElements( javaintArray, javaint1, 0 );
      }

      ( * a ).ReleaseIntArrayElements( d, javaint, 0 );

      free( image1 );
    }
    //Free the library:
    freeResult = FreeLibrary( dllHandle );
  }
  if ( !runTimeLinkSuccess ) cout << "Something Wrong" << endl;
}
