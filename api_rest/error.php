<?php
function set_error( $key, $str_err = '' ) {
	global $err_code, $err_label;
	if( DEBUG && !empty($str_err) ) {
		return array("status" => -$err_code[$key], "msg" => $str_err);
	} else {
		return array("status" => -$err_code[$key], "msg" => $err_label[$key]);
	}
}

$err_code = array(
	'SQL_CONNEXION'					=> 0x00000001,
	'SQL_NO_DATA'					=> 0x00000002,
	'SQL_PDO_PREPARE'				=> 0x00000004,
	'SQL_PDO_EXECUTE'				=> 0x00000008,
	'REQUEST_METHOD'				=> 0x00000010,
	'UNKNOWN_GET_QUERY'				=> 0x00000020,
	'UNKNOWN_POST_QUERY'				=> 0x00000040,
	'UNKNOWN_PUT_QUERY'				=> 0x00000080,
	'UNKNOWN_DELETE_QUERY'				=> 0x00000100,
	'ERR_0x00000200'				=> 0x00000200,
	'ERR_0x00000400'				=> 0x00000400,
	'ERR_0x00000800'				=> 0x00000800,
	'ERR_0x00001000'				=> 0x00001000,
	'ERR_0x00002000'				=> 0x00002000,
	'ERR_0x00004000'				=> 0x00004000,
	'ERR_0x00008000'				=> 0x00008000,
	'ERR_0x00010000'				=> 0x00010000,
	'ERR_0x00020000'				=> 0x00020000,
	'ERR_0x00040000'				=> 0x00040000,
	'ERR_0x00080000'				=> 0x00080000,

);
	
$err_label = array(
	'SQL_CONNEXION'					=> "MySQL Connexion Error",
	'SQL_NO_DATA'					=> "No SQL data found",
	'SQL_PDO_PREPARE'				=> "Error : PDO->prepare",
	'SQL_PDO_EXECUTE'				=> "Error : PDO->execute",
	'REQUEST_METHOD'				=> "Request method not supported",
	'UNKNOWN_GET_QUERY'				=> "Query GET is unknown",
	'UNKNOWN_POST_QUERY'				=> "Query POST is unknown",
	'UNKNOWN_PUT_QUERY'				=> "Query PUT is unknown",
	'UNKNOWN_DELETE_QUERY'				=> "Query DELETE is unknown",
	'CHECK_FIELD'					=> "Fields are empty or not declared",
	'ERR_0x00000400'				=> "Error 0x00000400",
	'ERR_0x00000800'				=> "Error 0x00000800",
	'ERR_0x00001000'				=> "Error 0x00001000",
	'ERR_0x00002000'				=> "Error 0x00002000",
	'ERR_0x00004000'				=> "Error 0x00004000",
	'ERR_0x00008000'				=> "Error 0x00008000",
	'ERR_0x00010000'				=> "Error 0x00010000",
	'ERR_0x00020000'				=> "Error 0x00020000",
	'ERR_0x00040000'				=> "Error 0x00040000",
	'ERR_0x00080000'				=> "Error 0x00080000",
);
?>