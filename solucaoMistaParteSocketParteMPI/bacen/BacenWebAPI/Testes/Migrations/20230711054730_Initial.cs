using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Domain.Migrations
{
    /// <inheritdoc />
    public partial class Initial : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "dimensao",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    dimensaoNome = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    dimensaoCodigo = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_dimensao", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "usuario",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    nome = table.Column<string>(type: "nvarchar(450)", nullable: false),
                    email = table.Column<string>(type: "nvarchar(450)", nullable: false),
                    login = table.Column<string>(type: "nvarchar(450)", nullable: false),
                    senha = table.Column<string>(type: "nvarchar(450)", nullable: false),
                    departamento = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    usuarioStatus_statusEnum = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_usuario", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "criterio",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    criterioNome = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    criterioCodigo = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    criterioDimensaoid = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_criterio", x => x.id);
                    table.ForeignKey(
                        name: "FK_criterio_dimensao_criterioDimensaoid",
                        column: x => x.criterioDimensaoid,
                        principalTable: "dimensao",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "adminSistema",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    tokenEmail = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_adminSistema", x => x.id);
                    table.ForeignKey(
                        name: "FK_adminSistema_usuario_id",
                        column: x => x.id,
                        principalTable: "usuario",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "papel",
                columns: table => new
                {
                    Usuarioid = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    valor = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_papel", x => new { x.Usuarioid, x.Id });
                    table.ForeignKey(
                        name: "FK_papel_usuario_Usuarioid",
                        column: x => x.Usuarioid,
                        principalTable: "usuario",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "professor",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    dataAdmissao = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_professor", x => x.id);
                    table.ForeignKey(
                        name: "FK_professor_usuario_id",
                        column: x => x.id,
                        principalTable: "usuario",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "atividade",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    atividadeCodigo = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    atividadeDescricao = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    atividadeIndicador = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    atividadePontos = table.Column<float>(type: "real", nullable: false),
                    AtividadeCriterioid = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    atividadeStatus_status = table.Column<string>(type: "text", nullable: false),
                    atividadeDataAlteracao = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_atividade", x => x.id);
                    table.ForeignKey(
                        name: "FK_atividade_criterio_AtividadeCriterioid",
                        column: x => x.AtividadeCriterioid,
                        principalTable: "criterio",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "hierarquia",
                columns: table => new
                {
                    Professorid = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    categoria = table.Column<string>(type: "text", nullable: false),
                    nivel = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_hierarquia", x => x.Professorid);
                    table.ForeignKey(
                        name: "FK_hierarquia_professor_Professorid",
                        column: x => x.Professorid,
                        principalTable: "professor",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "pontuacao",
                columns: table => new
                {
                    Professorid = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    dimensao1 = table.Column<float>(type: "real", nullable: false),
                    dimensao2 = table.Column<float>(type: "real", nullable: false),
                    dimensao3 = table.Column<float>(type: "real", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_pontuacao", x => x.Professorid);
                    table.ForeignKey(
                        name: "FK_pontuacao_professor_Professorid",
                        column: x => x.Professorid,
                        principalTable: "professor",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "declaracaoAtividade",
                columns: table => new
                {
                    id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    tituloDeclaracao = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    dataAlteracao = table.Column<DateTime>(type: "datetime2", nullable: false),
                    atividadeid = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    observacoes = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    professorPontos = table.Column<float>(type: "real", nullable: false),
                    comissaoPontos = table.Column<float>(type: "real", nullable: false),
                    professorid = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    statusDeclaracao_status = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_declaracaoAtividade", x => x.id);
                    table.ForeignKey(
                        name: "FK_declaracaoAtividade_atividade_atividadeid",
                        column: x => x.atividadeid,
                        principalTable: "atividade",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_declaracaoAtividade_professor_professorid",
                        column: x => x.professorid,
                        principalTable: "professor",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "documentoComprobatorio",
                columns: table => new
                {
                    declaracaoAtividadeid = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    fileName = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    hashName = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_documentoComprobatorio", x => new { x.declaracaoAtividadeid, x.Id });
                    table.ForeignKey(
                        name: "FK_documentoComprobatorio_declaracaoAtividade_declaracaoAtividadeid",
                        column: x => x.declaracaoAtividadeid,
                        principalTable: "declaracaoAtividade",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_atividade_AtividadeCriterioid",
                table: "atividade",
                column: "AtividadeCriterioid");

            migrationBuilder.CreateIndex(
                name: "IX_criterio_criterioDimensaoid",
                table: "criterio",
                column: "criterioDimensaoid");

            migrationBuilder.CreateIndex(
                name: "IX_declaracaoAtividade_atividadeid",
                table: "declaracaoAtividade",
                column: "atividadeid");

            migrationBuilder.CreateIndex(
                name: "IX_declaracaoAtividade_professorid",
                table: "declaracaoAtividade",
                column: "professorid");

            migrationBuilder.CreateIndex(
                name: "IX_usuario_email",
                table: "usuario",
                column: "email",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_usuario_login",
                table: "usuario",
                column: "login",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_usuario_nome",
                table: "usuario",
                column: "nome");

            migrationBuilder.CreateIndex(
                name: "IX_usuario_senha",
                table: "usuario",
                column: "senha");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "adminSistema");

            migrationBuilder.DropTable(
                name: "documentoComprobatorio");

            migrationBuilder.DropTable(
                name: "hierarquia");

            migrationBuilder.DropTable(
                name: "papel");

            migrationBuilder.DropTable(
                name: "pontuacao");

            migrationBuilder.DropTable(
                name: "declaracaoAtividade");

            migrationBuilder.DropTable(
                name: "atividade");

            migrationBuilder.DropTable(
                name: "professor");

            migrationBuilder.DropTable(
                name: "criterio");

            migrationBuilder.DropTable(
                name: "usuario");

            migrationBuilder.DropTable(
                name: "dimensao");
        }
    }
}
