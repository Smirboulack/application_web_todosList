/**
 * Placez ici les scripts qui seront exécutés côté client pour rendre l'application côté client fonctionnelle.
 */

// <editor-fold desc="Gestion de l'affichage">
/**
 * Fait basculer la visibilité des éléments affichés quand le hash change.<br>
 * Passe l'élément actif en inactif et l'élément correspondant au hash en actif.
 * @param hash une chaîne de caractères (trouvée a priori dans le hash) contenant un sélecteur CSS indiquant un élément à rendre visible.
 */
function show(hash) {
    const oldActiveElement = document.querySelector(".active");
    oldActiveElement.classList.remove("active");
    oldActiveElement.classList.add("inactive");
    const newActiveElement = document.querySelector(hash.split("/")[0]);
    newActiveElement.classList.remove("inactive");
    newActiveElement.classList.add("active");
}

/**
 * Affiche pendant 10 secondes un message sur l'interface indiquant le résultat de la dernière opération.
 * @param text Le texte du message à afficher
 * @param cssClass La classe CSS dans laquelle afficher le message (défaut = alert-info)
 */
function displayRequestResult(elementId, text, cssClass = "alert-info") {
    const requestResultElement = document.getElementById(elementId);
    requestResultElement.innerText = text;
    requestResultElement.classList.add(cssClass);
    setTimeout(
        () => {
            requestResultElement.classList.remove(cssClass);
            requestResultElement.innerText = "";
        }, 10000);
}


function renderTemplate(templateId, data, targetId) {
    const template = document.getElementById(templateId).innerHTML;
    const rendered = Mustache.render(template, data);
    document.getElementById(targetId).innerHTML = rendered;
}

function renderTemplateTable(templateId, data, targetId) {
    const template = document.getElementById(templateId).innerHTML;
    const rendered = Mustache.render(template, data);
    document.getElementById(targetId).innerHTML += rendered;
}

/**
 * Affiche ou cache les éléments de l'interface qui nécessitent une connexion.
 * @param isConnected un Booléen qui dit si l'utilisateur est connecté ou pas
 */
function displayConnected(isConnected) {
    const elementsRequiringConnection = document.getElementsByClassName("requiresConnection");
    const visibilityValue = isConnected ? "visible" : "collapse";
    for (const element of elementsRequiringConnection) {
        element.style.visibility = visibilityValue;
    }
}

function displayDisconnected(isConnected) {
    const elementsRequiringConnection = document.getElementsByClassName("requiresDisconnection");
    const visibilityValue = isConnected ? "visible" : "collapse";
    for (const element of elementsRequiringConnection) {
        element.style.visibility = visibilityValue;
    }
}

window.addEventListener('hashchange', () => {
    show(window.location.hash);

    switch (location.hash) {
        case '#monCompte':
            setTodosMonCompte();
            break;
        case '#todoList':
            setTodos();
            break;
        default:
            break;
    }
});
// </editor-fold>

// <editor-fold desc="Gestion des requêtes asynchrones">
/**
 * Met à jour le nombre d'utilisateurs de l'API sur la vue "index".
 */
function getNumberOfUsers() {
    const headers = new Headers();
    headers.append("Accept", "application/json");
    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };

    fetch(baseUrl + "users", requestConfig)
        .then((response) => {
            if (response.ok && response.headers.get("Content-Type").includes("application/json")) {
                return response.json();
            } else {
                throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
            }
        }).then((json) => {
            if (Array.isArray(json)) {
                renderTemplate('userCountTemplate', { nbUsers: json.length }, 'nbUsers');
            } else {
                throw new Error(json + " is not an array.");
            }
        }).catch((err) => {
            console.error("In getNumberOfUsers: " + err);
        });
}


function getAllTodos() {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Accept", "application/json");

    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors"
    };

    fetch(baseUrl + "todos", requestConfig)
        .then((response) => {
            if (response.status === 200) {
                return response.json();
            } else {
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then((todos) => {
            renderTemplate('todosCountTemplate', { nbTodos: todos.length }, 'nbTodos');
        })
        .catch((err) => {
            console.error("Erreur dans getAllTodos: " + err);
        });
}

/**
 * Met à jour les nombres d'utilisateurs et de todos de l'API sur la vue "index".
 */
function updateNumbersTodosUsers() {
    getAllTodos();
    getNumberOfUsers();
}

/**
 * Récupére les informations et todos de l'utilisateur
 *
 */
function setInfos(login) {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Accept", "application/json");

    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };

    fetch(baseUrl + "users/" + login + "/name", requestConfig)
        .then((response) => {
            if (response.status === 200) {
                //location.hash = "#monCompte";
                return response.json();
            }
        })
        .then((json) => {
            renderTemplate('userNameTemplate', { nom_update_input: json.name }, 'nom_update_input');
        })
        .catch((err) => {
            console.error("In setInfos: " + err);
        })
}

function setTodos() {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Accept", "application/json");

    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };

    fetch(baseUrl + "todos", requestConfig)
        .then((response) => {
            if (response.status === 200) {
                response.json()
                    .then((todoIds) => {
                        const todoTable = document.getElementById('tablebody');
                        todoTable.innerHTML = '';
                        todoIds.forEach(id =>
                            insertTodo(id)
                        );
                    });
            }
        })
        .catch((err) => {
            console.error("In setInfos: " + err);
        })
}

function updateTableRow(todoId) {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Accept", "application/json");

    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors"
    };

    fetch(baseUrl + "todos/" + todoId, requestConfig)
        .then((response) => {
            if (response.status === 200) {
                return response.json();
            } else {
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then((todo) => {
            const existingRow = document.getElementById('todoRow_' + todoId);
            if (existingRow) {
                const data = {
                    completed: todo.completed,
                    todoId: todoId,
                    title: todo.title,
                    assignee: todo.assignee == null ? 'Personne' : extractUsername(todo.assignee),
                    Isassignee: todo.assignee != null,
                    btn_clickable: todo.completed ? 'disabled' : 'enabled'
                };
                const rendered = Mustache.render(document.getElementById('tableTemplate').innerHTML, data);
                existingRow.outerHTML = rendered;
            }
        })
        .catch((err) => {
            console.error("Erreur dans updateTableRow: " + err);
        });
}

function extractUsername(assignee) {
    return assignee.split('/')[1];
}

function extractTodoId(todo) {
    return todo.split('/')[1];
}

function insertTodo(todoId) {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Accept", "application/json");

    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors"
    };

    fetch(baseUrl + "todos/" + todoId, requestConfig)
        .then((response) => {
            if (response.status === 200) {
                return response.json();
            } else {
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then((todo) => {
            const data = {
                completed: todo.completed,
                todoId: todoId,
                title: todo.title,
                assignee: todo.assignee == null ? 'Personne' : extractUsername(todo.assignee),
                Isassignee: todo.assignee != null,
                btn_clickable: todo.completed ? 'disabled' : 'enabled'
            };
            renderTemplateTable('tableTemplate', data, 'tablebody');
        })
        .catch((err) => {
            console.error("Erreur dans insertTodo: " + err);
        });
}

function assignerTodo(todoId) {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Content-Type", "application/json");

    const login = sessionStorage.getItem('login');

    const body = {
        assignee: login
    };

    const requestConfig = {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors"
    };

    fetch(baseUrl + "todos/" + todoId, requestConfig)
        .then((response) => {
            if (response.status === 204) {
                updateTableRow(todoId);
                displayRequestResult("requestResult-todos", "Assignation du todo réussie", "alert-success");
            } else {
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
}

function retirerTodo(todoId) {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Content-Type", "application/json");

    const body = {
        assignee: "null"
    };

    const requestConfig = {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors"
    };

    fetch(baseUrl + "todos/" + todoId, requestConfig)
        .then((response) => {
            if (response.status === 204) {
                displayRequestResult("requestResult-todos", "Retrait du todo réussi", "alert-success");
            } else {
                throw new Error("Bad response code (" + response.status + ").");
            }
        }).then(() => {
            updateTableRow(todoId);
        });
}


function supprimerTodo(todoId) {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));

    const requestConfig = {
        method: "DELETE",
        headers: headers,
        mode: "cors"
    };

    fetch(baseUrl + "todos/" + todoId, requestConfig)
        .then((response) => {
            if (response.status === 204) {
                const todoRow = document.getElementById('todoRow_' + todoId);
                if (todoRow) {
                    todoRow.remove();
                }
                displayRequestResult("requestResult-todos", "Suppression du todo réussie", "alert-success");
            } else {
                throw new Error("Erreur lors de la suppression du todo (Code: " + response.status + ").");
            }
        })
        .catch((err) => {
            console.error("Erreur lors de la suppression du todo: " + err);
        });
}

function deleteAllTodos() {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Accept", "application/json");

    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };

    fetch(baseUrl + "todos", requestConfig)
        .then((response) => {
            if (response.status === 200) {
                response.json()
                    .then((todoIds) => {
                        todoIds.forEach(id => supprimerTodo(id));
                    })
            }
        })
        .catch((err) => {
            console.error("In setInfos: " + err);
        })
}

/**
 * Permet de mettre à jour l'état completed du todo
 *
 */
function completerTodo(todoId) {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Content-Type", "application/json");

    const body = {
        hash: todoId
    };

    const requestConfig = {
        method: "POST",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors"
    };

    fetch(baseUrl + "todos/toggleStatus", requestConfig)
        .then((response) => {
            if (response.status === 204) {
                updateTableRow(todoId);
                displayRequestResult("requestResult-todos", "Mise à jour du todo réussie", "alert-success");
            } else {
                throw new Error("Erreur lors de la mise à jour du todo (Code: " + response.status + ").");
            }
        })
        .catch((err) => {
            console.error("Erreur lors de la mise à jour du todo: " + err);
        });
}



/**
 * Permet de mettre à jour le nom
 *
 */

function majNom() {
    const nom = document.getElementById('nom_update_input').innerHTML.trim();
    const login = document.getElementById('login').innerHTML.trim();

    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Content-Type", "application/json");

    const body = {
        name: nom
    };

    const requestConfig = {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client
    };

    fetch(baseUrl + "users/" + login, requestConfig)
        .then((response) => {
            if (response.status === 204) {
                location.hash = "#monCompte";
                setInfos(login);
                displayRequestResult("requestResult-general", "Modification de nom réussie", "alert-success");
            } else {
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
}


/**
 * Permet de mettre à jour le mot de passe
 *
 */

function majMdp() {
    const mdp = document.getElementById('password_update_input').value;
    const login = document.getElementById('login').innerHTML.trim();

    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Content-Type", "application/json");

    const body = {
        password: mdp
    };

    const requestConfig = {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client
    };

    fetch(baseUrl + "users/" + login, requestConfig)
        .then((response) => {
            if (response.status === 204) {
                location.hash = "#monCompte";
                displayRequestResult("requestResult-general", "Modification de mot de passe réussie", "alert-success");
            } else {
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
}

/**
 * Creer un todo
 *
 */
function creerTodo() {
    const titre = document.getElementById('text').value;
    const login = document.getElementById('login').innerHTML.trim();

    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Content-Type", "application/json");

    const body = {
        title: titre,
        creator: login
    };

    const requestConfig = {
        method: "POST",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client
    };

    fetch(baseUrl + "todos/", requestConfig)
        .then((response) => {
            if (response.status === 201) {
                getAllTodos();
                setTodos();
                displayRequestResult("requestResult-todos", "Création du todo réussie", "alert-success");
            } else {
                throw new Error("Bad response code (" + response.status + ").");
            }
        }).catch((err) => {
            console.error("In creerTodo: " + err);
        });
}

function getTodoById(todoId) {
    const headers = new Headers();
    headers.append("Authorization", "Bearer " + sessionStorage.getItem('accessToken'));
    headers.append("Accept", "application/json"); // Supposons que nous voulons une réponse JSON

    const requestConfig = {
        method: "GET",
        headers: headers
    };

    fetch(baseUrl + "/todos/" + todoId, requestConfig)
        .then(response => {
            if (response.status === 200) {
                return response.json();
            } else if (response.status === 400) {
                throw new Error("Id du todo syntaxiquement incorrect (non entier)");
            } else if (response.status === 401) {
                throw new Error("Utilisateur non authentifié");
            } else if (response.status === 404) {
                throw new Error("Todo non trouvé");
            } else {
                throw new Error("Erreur non spécifiée");
            }
        })
        .then(data => {
            console.log("Todo récupéré: ", data);
            return data;
        })
        .catch(error => {
            console.error(error);
        });
}


/**
 * Envoie la requête de login en fonction du contenu des champs de l'interface.
 */
function connect() {

    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    const body = {
        login: document.getElementById("login_input").value,
        password: document.getElementById("password_input").value
    };

    const requestConfig = {
        method: "POST",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };

    fetch(baseUrl + "users/login", requestConfig)
        .then((response) => {
            if (response.status === 204) {
                location.hash = "#index";
                const jwt = response.headers.get("Authorization");
                sessionStorage.setItem("accessToken", jwt);
                sessionStorage.setItem('login', body.login);
                renderTemplate('userLoginTemplate', { login: body.login }, 'login');
                renderTemplate("usernameBonjourTemplate", {username: body.login}, "username");
                displayConnected(true);
                displayDisconnected(false);
                displayRequestResult("requestResult-general", "Connexion réussie", "alert-success");
                setInfos(body.login);
                setTodos();
            } else {
                displayRequestResult("requestResult-general", "Connexion refusée ou impossible", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .catch((err) => {
            console.error("In connect: " + err);
        })
}

function deco() {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Content-Type", "application/json");
    const body = {};
    const requestConfig = {
        method: "POST",
        headers: headers,
        mode: "cors",
        body: JSON.stringify(body),
    };
    fetch(baseUrl + "users/logout", requestConfig)
        .then((response) => {
            if (response.status === 204) {
                sessionStorage.removeItem('accessToken');
                location.hash = "#index";
                displayConnected(false);
                displayDisconnected(true);
                displayRequestResult("requestResult-general", "Déconnexion réussie", "alert-success");
            } else {
                displayRequestResult("requestResult-general", "Déconnexion impossible", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .catch((err) => {
            console.error("In login: " + err);
        })
}

function getUserAssignedTodos(userId) {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Accept", "application/json");

    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors"
    };

    return fetch(baseUrl + "users/" + userId + "/assignedTodos", requestConfig)
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la récupération des todos assignés (Code: " + response.status + ")");
            }
            return response.json();
        })
        .then(json => {
            console.log("Récupération des todos assignés réussie");
            return json.assignedTodos;
        })
        .catch(err => {
            console.error("Erreur dans getUserAssignedTodos: " + err);
            throw err;
        });
}



function getTodoTitle(todoId) {
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Content-Type", "application/json");

    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors"
    };

    return fetch(baseUrl + "todos/" + todoId + "/title", requestConfig)
        .then(response => {
            if (response.status === 200) {
                return response.json();
            } else {
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then(data => {
            return data.title;
        })
        .catch(err => {
            console.error("In getTodoTitle: " + err);
            throw err;
        });
}



/**
 * Mettre à jour les todos de l'user en cours dans la vue Mon compte
 */
function setTodosMonCompte() {
    const login = sessionStorage.getItem('login');
    const headers = new Headers();
    headers.append("Authorization", sessionStorage.getItem('accessToken'));
    headers.append("Accept", "application/json");

    fetch(baseUrl + "users/" + login + "/assignedTodos", { method: "GET", headers: headers, mode: "cors" })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la récupération des todos assignés (Code: " + response.status + ")");
            }
            return response.json();
        })
        .then(json => {
            const todos = json.assignedTodos;
            const promises = todos.map(todo => {
                const todoId = extractTodoId(todo);
                return fetch(baseUrl + "todos/" + todoId + "/title", { method: "GET", headers: headers, mode: "cors" })
                    .then(response => {
                        if (response.status === 200) {
                            return response.json();
                        } else {
                            throw new Error("Bad response code (" + response.status + ") for todo ID: " + todoId);
                        }
                    })
                    .then(data => data.title)
                    .catch(err => console.error("Erreur dans la récupération du titre: " + err));
            });
            return Promise.all(promises);
        })
        .then(titles => {
            const d = document.getElementById('TodosMonCompte');
            d.innerHTML = '';
            titles.forEach(title => {
                if (title) {
                    const data = { title: title };
                    renderTemplateTable('todoMonCompteTemplate', data, 'TodosMonCompte');
                }
            });
        })
        .catch(err => {
            console.error("Erreur dans setTodosMonCompte: " + err);
        });
}

function getUsername(login, event) {
    if (login !== "Personne") {
        const headers = new Headers();
        headers.append("Authorization", sessionStorage.getItem('accessToken'));
        headers.append("Accept", "application/json");

        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors"
        };

        fetch(baseUrl + "users/" + login + "/name", requestConfig)
            .then((response) => {
                if (response.status === 200) {
                    return response.json();
                } else {
                    throw new Error("Erreur lors de la récupération du nom d'utilisateur");
                }
            })
            .then((json) => {

                const data = { username: json.name };
                renderTemplate('popupTemplate', data, 'popup');
                afficherPopup(event);

            })
            .catch((err) => {
                console.error("Erreur dans getUsername: " + err);
            });
    }
}

function afficherPopup(event) {
    var popup = document.getElementById("popup");
    popup.style.left = event.clientX + 5 + 'px';
    popup.style.top = event.clientY + 5 + 'px';
    popup.style.display = "block";
}

function masquerPopup() {
    var popup = document.getElementById("popup");
    popup.style.display = "none";
}

function editTitle(tdElement, todoId) {
    if (tdElement.querySelector('input')) return;
    // Sauvegarde le texte actuel
    var originalText = tdElement.innerHTML;

    // Crée un nouvel élément input
    var input = document.createElement('input');
    input.type = 'text';
    input.value = originalText;
    input.size = Math.max(originalText.length / 2, 5);
    input.onblur = function () {
        // Vérifie si la valeur a changé
        // Met à jour le texte dans le <td> seulement si la valeur a changé
        if (this.value !== originalText) {

            tdElement.innerHTML = this.value;
            const headers = new Headers();
            headers.append("Authorization", sessionStorage.getItem('accessToken'));
            headers.append("Content-Type", "application/json");

            const body = {
                title: this.value
            };

            const requestConfig = {
                method: "PUT",
                headers: headers,
                body: JSON.stringify(body),
                mode: "cors"
            };

            fetch(baseUrl + "todos/" + todoId, requestConfig)
                .then((response) => {
                    if (response.status === 204) {
                        updateTableRow(todoId);
                        displayRequestResult("requestResult-todos", "Mise à jour du titre réussie", "alert-success");
                    } else {
                        throw new Error("Bad response code (" + response.status + ").");
                    }
                })
                .catch((err) => {
                    console.error("Erreur dans editTitle: " + err);
                });
        } else {
            tdElement.innerHTML = originalText;
        }
    };

    tdElement.innerHTML = '';
    tdElement.appendChild(input);

    input.focus();
}



setInterval(updateNumbersTodosUsers, 5000);
setInterval(setTodos, 5000);
// </editor-fold>