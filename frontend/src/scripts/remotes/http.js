// --- CLIENTE HTTP GENÉRICO USADO POR TODOS OS REMOTES ---
const BASE_API = "/api";

// --- LÊ O TOKEN ARMAZENADO ---
function obterToken() {
    return localStorage.getItem("token");
}

// --- MONTA OS CABEÇALHOS DA REQUISIÇÃO (JSON + AUTENTICAÇÃO) ---
function montarCabecalhos(extras = {}, ehJson = true) {
    const cabecalhos = { ...extras };
    if (ehJson) {
        cabecalhos["Content-Type"] = "application/json";
    }
    const token = obterToken();

    if (token) {
        cabecalhos["Authorization"] = `Bearer ${token}`;
    }
    return cabecalhos;
}

// --- TRATA A RESPOSTA E LANÇA ERRO QUANDO NÃO OK ---
async function tratarResposta(resposta) {
    if (resposta.status === 204) {
        return null;
    }
    const texto = await resposta.text();
    const dados = texto ? JSON.parse(texto) : null;

    if (!resposta.ok) {
        const mensagem = (dados && (dados.message || dados.error)) || `Erro ${resposta.status}`;
        throw new Error(mensagem);
    }
    return dados;
}

export const http = {
    get: (caminho, parametros) => {
        const queryString = parametros ? "?" + new URLSearchParams(parametros).toString() : "";
        return fetch(`${BASE_API}${caminho}${queryString}`, {
            headers: montarCabecalhos()
        }).then(tratarResposta);
    },

    post: (caminho, corpo) =>
        fetch(`${BASE_API}${caminho}`, {
            method: "POST",
            headers: montarCabecalhos(),
            body: corpo ? JSON.stringify(corpo) : undefined
        }).then(tratarResposta),

    put: (caminho, corpo) =>
        fetch(`${BASE_API}${caminho}`, {
            method: "PUT",
            headers: montarCabecalhos(),
            body: corpo ? JSON.stringify(corpo) : undefined
        }).then(tratarResposta),

    del: (caminho) =>
        fetch(`${BASE_API}${caminho}`, {
            method: "DELETE",
            headers: montarCabecalhos()
        }).then(tratarResposta),

    // --- ENVIO MULTIPART ---
    postMultipart: (caminho, dadosObjeto, arquivo, campoArquivo = "arquivo", campoDados = "dados") => {
        const formData = new FormData();
        formData.append(campoDados, new Blob([JSON.stringify(dadosObjeto)], {
            type: "application/json"
        }));

        if (arquivo) {
            formData.append(campoArquivo, arquivo);
        }
        return fetch(`${BASE_API}${caminho}`, {
            method: "POST",
            headers: montarCabecalhos({}, false),
            body: formData
        }).then(tratarResposta);
    },

    putMultipart: (caminho, dadosObjeto, arquivo, campoArquivo = "arquivo", campoDados = "dados") => {
        const formData = new FormData();
        formData.append(campoDados, new Blob([JSON.stringify(dadosObjeto)], {
            type: "application/json"
        }));

        if (arquivo) {
            formData.append(campoArquivo, arquivo);
        }
        return fetch(`${BASE_API}${caminho}`, {
            method: "PUT",
            headers: montarCabecalhos({}, false),
            body: formData
        }).then(tratarResposta);
    }
};

