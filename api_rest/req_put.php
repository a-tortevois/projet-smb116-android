<?php
// if( DEBUG ) echo "REQUEST_METHOD : PUT <br />";
switch( count($query) ) {
	case 2 :
		switch( $query[0] ) {
			// PUT ~/customers/$id
			case 'customers' :
				$s_sql = "SELECT * FROM smb116_customer WHERE id_customer = :id";
				if( $s_req = $pdo->prepare($s_sql) ) {
					$s_req->bindValue(':id', $id, PDO::PARAM_INT);
					if( $s_req->execute() ) {
						if($s_req->rowCount() > 0) {
							$fields = array();
							if( is_var($json_in->contract_id) )
								$fields[].= 'contract_id = :contract_id';
							if( is_var($json_in->contract_key) ) 
								$fields[].= 'contract_key = :contract_key';
							if( is_var($json_in->lastname) ) 
								$fields[].= 'lastname = :lastname';
							if( is_var($json_in->name) ) 
								$fields[].= 'name = :name';
							
							$sql = 'UPDATE smb116_customer SET '. implode(', ', $fields) . ' WHERE id_customer = :id';
							// if( DEBUG ) print_var($sql);
							if( $req = $pdo->prepare($sql) ) {
								if( is_var($json_in->contract_id) )
									$req->bindValue(':contract_id', $json_in->contract_id, PDO::PARAM_STR);
								if( is_var($json_in->contract_key) ) 
									$req->bindValue(':contract_key', $json_in->contract_key, PDO::PARAM_STR);
								if( is_var($json_in->lastname) ) 
									$req->bindValue(':lastname', $json_in->lastname, PDO::PARAM_STR);
								if( is_var($json_in->name) ) 
									$req->bindValue(':name', $json_in->name, PDO::PARAM_STR);					
							}
						} else {
							$json_out = set_error('SQL_NO_DATA'); // unknown ID
						}
					} else {
						$error = $pdo->errorInfo();
						$json_out = set_error('SQL_PDO_EXECUTE', $error[2]);
					}
				} else {
					$error = $pdo->errorInfo();
					$json_out = set_error('SQL_PDO_PREPARE', $error[2]);
				}
			break;

			// PUT ~/equipments/$id
			case 'equipments' :
				$s_sql = "SELECT * FROM smb116_equipment WHERE id_equipment = :id";
				if( $s_req = $pdo->prepare($s_sql) ) {
					$s_req->bindValue(':id', $id, PDO::PARAM_INT);
					if( $s_req->execute() ) {
						if($s_req->rowCount() > 0) {
							$fields = array();
							if( is_var($json_in->id_customer) )
								$fields[].= 'id_customer = :id_customer';
							if( is_var($json_in->serial_number) ) 
								$fields[].= 'serial_number = :serial_number';
							if( is_var($json_in->comment) ) 
								$fields[].= 'comment = :comment';
							if( is_var($json_in->lat) ) 
								$fields[].= 'lat = :lat';
							if( is_var($json_in->lng) ) 
								$fields[].= 'lng = :lng';
							
							$sql = 'UPDATE smb116_equipment SET '. implode(', ', $fields) . ' WHERE id_equipment = :id';
							// if( DEBUG ) print_var($sql);
							if( $req = $pdo->prepare($sql) ) {
								if( is_var($json_in->id_customer) )
									$req->bindValue(':id_customer', $json_in->id_customer, PDO::PARAM_STR);
								if( is_var($json_in->serial_number) ) 
									$req->bindValue(':serial_number', $json_in->serial_number, PDO::PARAM_STR);
								if( is_var($json_in->comment) ) 
									$req->bindValue(':comment', $json_in->comment, PDO::PARAM_STR);
								if( is_var($json_in->lat) ) 
									$req->bindValue(':lat', $json_in->lat, PDO::PARAM_STR);					
								if( is_var($json_in->lng) ) 
									$req->bindValue(':lng', $json_in->lng, PDO::PARAM_STR);	
							}
						} else {
							$json_out = set_error('SQL_NO_DATA'); // unknown ID
						}
					} else {
						$error = $pdo->errorInfo();
						$json_out = set_error('SQL_PDO_EXECUTE', $error[2]);
					}
				} else {
					$error = $pdo->errorInfo();
					$json_out = set_error('SQL_PDO_PREPARE', $error[2]);
				}
			break;

			// PUT ~/tickets/$id
			case 'tickets' :
				$s_sql = "SELECT * FROM smb116_ticket WHERE id_ticket = :id";
				if( $s_req = $pdo->prepare($s_sql) ) {
					$s_req->bindValue(':id', $id, PDO::PARAM_INT);
					if( $s_req->execute() ) {
						if($s_req->rowCount() > 0) {							
							$fields = array();
							if( is_var($json_in->id_equipment) )
								$fields[].= 'id_equipment = :id_equipment';
							if( is_var($json_in->subject) ) 
								$fields[].= 'subject = :subject';
							if( is_numeric($json_in->status) ) 
								$fields[].= 'status = :status';
							if( is_var($json_in->date_status) ) 
								$fields[].= 'date_status = :date_status';
							
							$sql = 'UPDATE smb116_ticket SET '. implode(', ', $fields) . ' WHERE id_ticket = :id';
							// if( DEBUG ) print_var($sql);
							if( $req = $pdo->prepare($sql) ) {
								if( is_var($json_in->id_equipment) )
									$req->bindValue(':id_equipment', $json_in->id_equipment, PDO::PARAM_STR);
								if( is_var($json_in->subject) ) 
									$req->bindValue(':subject', $json_in->subject, PDO::PARAM_STR);
								if( is_numeric($json_in->status) ) 
									$req->bindValue(':status', $json_in->status, PDO::PARAM_STR);
								if( is_var($json_in->date_status) ) 
									$req->bindValue(':date_status', $json_in->date_status, PDO::PARAM_STR);
							}
						} else {
							$json_out = set_error('SQL_NO_DATA'); // unknown ID
						}
					} else {
						$error = $pdo->errorInfo();
						$json_out = set_error('SQL_PDO_EXECUTE', $error[2]);
					}
				} else {
					$error = $pdo->errorInfo();
					$json_out = set_error('SQL_PDO_PREPARE', $error[2]);
				}
			break;
				
			// PUT ~/messages/$id
			case 'messages' :
				$s_sql = "SELECT * FROM smb116_message WHERE id_message = :id";
				if( $s_req = $pdo->prepare($s_sql) ) {
					$s_req->bindValue(':id', $id, PDO::PARAM_INT);
					if( $s_req->execute() ) {
						if($s_req->rowCount() > 0) {
							$fields = array();
							if( is_var($json_in->id_ticket) )
								$fields[].= 'id_ticket = :id_ticket';
							if( is_var($json_in->id_customer) ) 
								$fields[].= 'id_customer = :id_customer';
							if( is_var($json_in->date_message) ) 
								$fields[].= 'date_message = :date_message';
							if( is_var($json_in->content) ) 
								$fields[].= 'content = :content';
							
							$sql = 'UPDATE smb116_message SET '. implode(', ', $fields) . ' WHERE id_message = :id';
							// if( DEBUG ) print_var($sql);
							if( $req = $pdo->prepare($sql) ) {
								if( is_var($json_in->id_ticket) )
									$req->bindValue(':id_ticket', $json_in->id_ticket, PDO::PARAM_STR);
								if( is_var($json_in->id_customer) ) 
									$req->bindValue(':id_customer', $json_in->id_customer, PDO::PARAM_STR);
								if( is_var($json_in->date_message) ) 
									$req->bindValue(':date_message', $json_in->date_message, PDO::PARAM_STR);
								if( is_var($json_in->content) ) 
									$req->bindValue(':content', $json_in->content, PDO::PARAM_STR);
							}
						} else {
							$json_out = set_error('SQL_NO_DATA'); // unknown ID
						}
					} else {
						$error = $pdo->errorInfo();
						$json_out = set_error('SQL_PDO_EXECUTE', $error[2]);
					}
				} else {
					$error = $pdo->errorInfo();
					$json_out = set_error('SQL_PDO_PREPARE', $error[2]);
				}
			break;
		}
	break;
}

if( $json_out['status'] == 0 ) { // no error
	if( !empty($sql) ) {
		// if( DEBUG ) echo $sql;
		if( $req ) {
			$req->bindValue(':id', $id, PDO::PARAM_INT);	
			if( $req->execute() ) {
				// data inserted successfully
				switch( $query[0] ) {
					case 'customers' :
						$key = 'id_customer';
					break;

					case 'equipments' :
						$key = 'id_equipment';
					break;

					case 'tickets' :
						$key = 'id_ticket';
					break;
					
					case 'messages' :
						$key = 'id_message';
					break;
				}
				$json_out['status'] = "1";
				$json_out['msg'] = array( $key => $id );
			} else {
				// data not inserted
				$error = $pdo->errorInfo();
				$json_out = set_error('SQL_PDO_EXECUTE', $error[2]); 
			}
		} else {
			$error = $pdo->errorInfo();
			$json_out = set_error('SQL_PDO_PREPARE', $error[2]);
		}
	} else {
		$json_out = set_error('UNKNOWN_PUT_QUERY');
	}
}
?>