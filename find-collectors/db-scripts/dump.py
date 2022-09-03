from time import sleep
from typing import List, TextIO
import psycopg2
import os

POSTGRES_USER = os.getenv('POSTGRES_USER') if os.getenv(
    'POSTGRES_USER') else 'postgres'
POSTGRES_PASSWORD = os.getenv('POSTGRES_PASSWORD') if os.getenv(
    'POSTGRES_PASSWORD') else 'postgres'
POSTGRES_DB = os.getenv('POSTGRES_DB') if os.getenv(
    'POSTGRES_DB') else 'find-collectors'
POSTGRES_HOST = os.getenv('POSTGRES_HOST') if os.getenv('POSTGRES_HOST') else 'localhost'


def dump_database() -> None:
    """
    Realiza a conexão com o banco de dados e insere os dados que foram obtidos no arquivo de script do banco.
    """

    print('Iniciando o dump do banco de dados')

    commands = _read_script()

    with psycopg2.connect(dbname=POSTGRES_DB, user=POSTGRES_USER, password=POSTGRES_PASSWORD, host=POSTGRES_HOST) \
            as conn:
        with conn.cursor() as cursor:
            for command in commands:
                try:
                    cursor.execute(command)
                    conn.commit()
                except Exception:
                    pass
    
    print('Dump finalizado')


def _set_up_querys(file: TextIO, commands: List[str]) -> None:
    """
    Recebe o arquivo que foi aberto e faz uma varredura sobre as linhas em busca do caracter ';' para identificar o
    final da query.

        Parameters:
            file(TextIO): O arquivo que contem as querys que serao executadas
            commands(List[str]): o array de comando que foi montado com a query

        Returns:
            Retorno implicito do comandos que foram gerados atraves do parametro commands

    """
    
    query: str = ''
    for line in file:
        query += line
        if ';' in line:
            commands.append(query)
            query = ''


def _read_script() -> List[str]:
    """
    Le os arquivo do banco de dados e salva os comandos que devem ser realizados em uma lista de commandos.

        Returns:
            commands List[str]: Lista de comando sql que devem ser realizados.
    """
    
    commands = []
    with open('dump.sql', 'r') as db:
        _set_up_querys(db, commands)

    with open('popular-estados-cidades.sql', 'r') as populate:
        _set_up_querys(populate, commands)

    return commands


def test_connection() -> None:
    """
    Teste conexao ate que o banco de dados tenha se inicializado por completo para começar a realizar o dump do banco.
    """
    
    while True:
        sleep(5)
        conn = None
        try:
            conn = psycopg2.connect(dbname=POSTGRES_DB, user=POSTGRES_USER, password=POSTGRES_PASSWORD,
                                    host=POSTGRES_HOST)
        except Exception as e:
            print('Banco de dados ainda não está pronto')
            print(str(e))
            pass
        finally:
            if conn:
                conn.close()
                break


if __name__ == '__main__':
    test_connection()
    dump_database()
