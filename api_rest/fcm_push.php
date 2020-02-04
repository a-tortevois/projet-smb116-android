<?php
// API access key from Google API's Console
define( 'API_ACCESS_KEY', '' );

// Prepare bundle
$notification = array(
	'title'		=> 'Ticket #'.$id_ticket,
	'body'		=> 'You have a new message',
);

$data = array(
	'id_message'	=> $id_message,
	'id_ticket'		=> $id_ticket,
	'id_customer'	=> $id_customer,
);

$fcm = array(
	'to'			=> $topic,
	'notification'	=> $notification,
	'data'			=> $data,
);
 
$headers = array(
	'Authorization: key=' . API_ACCESS_KEY,
	'Content-Type: application/json'
);
 
$ch = curl_init();
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
curl_setopt( $ch, CURLOPT_POST, true );
curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers );
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false );
curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fcm ) );
$json_out['fcm'] = curl_exec($ch );
curl_close( $ch );
?>