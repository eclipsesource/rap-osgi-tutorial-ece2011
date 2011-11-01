
var names = [
  'rwt',
  'jetty',
  'http_jetty',
  'rwt_osgi',
  'port_9090',
  'port_9091',
  'example_bundles',
  'example_app',
  'admin_app',
  'example_9090',
  'example_9091',
  'admin_9090',
  'admin_9091',
  'example_app_conf',
  'admin_app_conf'
];

var regions = [
  [ 5, 4, 65, 17, 'jetty' ],
  [ 5, 18, 65, 25, 'http_jetty' ],
  [ 5, 26, 25, 38, 'port_9090' ],
  [ 26, 26, 46, 38, 'port_9091' ],
  [ 5, 84, 65, 96, 'rwt' ],
  [ 5, 70, 65, 83, 'rwt_osgi' ],
  [ 69, 69, 95, 96, 'example_bundles' ],
  [ 65, 41, 93, 51, 'example_app' ],
  [ 65, 52, 93, 62, 'admin_app' ],
  [ 53, 48, 65, 54, 'example_app_conf' ],
  [ 53, 58, 65, 64, 'admin_app_conf' ]
];

var createLayer = function( name ) {
  var parent = $( '#images' );
  var image = $( '<img class="layer" id="' + name + '" src="' + name + '.png"/>' );
  image.hide();
  parent.append( image );
}

var setVisibility = function( name, show ) {
  var element = $( '#' + name );
  if( element.is( ":visible" ) != show ) {
    element.fadeToggle( 'slow' );
  }
}

var adjustVisibility = function() {
  var port1 = $( '#port_9090' ).is( ":visible" );
  var port2 = $( '#port_9091' ).is( ":visible" );
  var example_app = $( '#example_app' ).is( ":visible" );
  var admin_app = $( '#admin_app' ).is( ":visible" );
  var example_app_conf = $( '#example_app_conf' ).is( ":visible" );
  var admin_app_conf = $( '#admin_app_conf' ).is( ":visible" );
  setVisibility( 'example_9090', port1 && example_app );
  setVisibility( 'example_9091', port2 && example_app && !example_app_conf );
  setVisibility( 'admin_9090', port1 && admin_app && !admin_app_conf );
  setVisibility( 'admin_9091', port2 && admin_app );
}

var handleClick = function( event ) {
  var x = event.layerX / $( event.target ).width() * 100;
  var y = event.layerY / $( event.target ).height() * 100;
  console.log( x, y, event );
  for( var i = 0; i < regions.length; i++ ) {
    var region = regions[ i ];
    if( x >= region[ 0 ] && y >= region[1] && x <= region[2] && y <= region[3] ) {
      $( '#' + region[4] ).fadeToggle( 'fast', function() {
        adjustVisibility();
      } );
      break;
    }
  }
}

var layout = function( name ) {
  var body = $( 'body' );
  var width  = body.width();
  var height = body.height();
  var boxWidth = height * 1476 / 1107;
  var boxHeight = height;
  var offset = ( width - boxWidth ) / 2;
  console.log( "layout", width, height, boxWidth, boxHeight );
  $( '#images' ).css( { left: offset, top: 0, width: boxWidth, height: boxHeight } );
  $( '.layer' ).css( { left: 0, top: 0, width: boxWidth, height: boxHeight } );
}

$( document ).ready( function() {
  for( var i = 0; i < names.length; i++ ) {
    createLayer( names[ i ] );
  }
  $( '#images' ).click( function( event ) {
      handleClick( event ); 
  } );
  $( window ).resize( function() {
    layout();
  } );
  layout();
} );
