<?php
// source : https://www.webtutorials.me/simple-crud-rest-api-php-pdo/
header('Content-Type: application/json');

$host = "tortevois.mysql.db";	// serveur
$user = ""; 					// nom d'utilisateur
$pass = "";	 					// mot de passe
$dbb  = ""; 					// nom de la base 

// Connexion à la base MySQL
$dsn = 'mysql:host='.$host.';dbname='.$dbb;

define( 'DEBUG', false );

// Le tableau de sortie
$json_out = [];
$json_out['status'] = 0;
$json_out['msg'] = array();

include("error.php");

try {
    $pdo = new PDO($dsn, $user, $pass);
	$query = explode( "/", $_GET['q']);
	$json_in = json_decode(file_get_contents("php://input"));
	if( is_var($json_in) ) {
		foreach ($json_in as $key => $value) {
			$json_in->$key = safe_var($value);
		}
	}
	
	$id = null;
	if( count($query) > 1 ) {
		if( is_numeric($query[1]) ) {
			$id = intval( $query[1] );
		}
	}
	
	$sql = '';
	if( is_var( $query ) ) {
		switch( $_SERVER["REQUEST_METHOD"] ) { 
			case "GET" :
				include("req_get.php");
			break;

			case "POST" :
				include("req_post.php");
			break;
			
			case "PUT" :
				if( $id != null ) {
					if( count((array)$json_in) > 1 ) {
						include("req_put.php");
					} else {
						// no field to update ?
					}
				} else {
					$json_out = set_error('CHECK_FIELD'); // no ID
				}
			break;

			case "DELETE" :
				if( $id != null ) {
					include("req_delete.php");
				} else {
					$json_out = set_error('CHECK_FIELD'); // no ID
				}
			break;

			default : $json_out = set_error('REQUEST_METHOD');
		}
	}
} catch (PDOException $e) {
    $json_out = set_error('SQL_CONNEXION'); //$e->getMessage()
} finally {
	echo json_encode($json_out);	
}

// contre les injections XSS
function safe_var( $var ) {
	return htmlspecialchars(strip_tags($var));
}

// test si la variable existe et n'est pas vide
function is_var( $var ) {
	if( isset($var) ) {
		if( !empty($var) ){
			return true;
		}
	}
	return false;
}

// Pour le DEBUG
function print_var( $var ) {
	global $$var;
	echo $var." = ";
	var_dump($$var);
	echo "<br />";
}
?>