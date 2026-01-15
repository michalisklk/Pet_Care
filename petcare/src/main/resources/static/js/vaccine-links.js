document.addEventListener("DOMContentLoaded", () => {

    const petSelect = document.getElementById("petId");
    const vaccinesLink = document.getElementById("pcVaccinesLink");

    if (petSelect && vaccinesLink) {
        const update = () => {
            const petId = (petSelect.value || "").trim();

            if (!petId) {
                vaccinesLink.setAttribute("href", "#");
                vaccinesLink.setAttribute("aria-disabled", "true");
                vaccinesLink.classList.add("is-disabled");
                return;
            }

            vaccinesLink.setAttribute("href", `/vaccines/recommendations?petId=${encodeURIComponent(petId)}`);
            vaccinesLink.setAttribute("aria-disabled", "false");
            vaccinesLink.classList.remove("is-disabled");
        };

        petSelect.addEventListener("change", update);
        update();
    }

    const speciesSelect = document.getElementById("species");
    const ageInput = document.getElementById("age");
    const previewLink = document.getElementById("pcVaccinesPreviewLink");

    if (speciesSelect && ageInput && previewLink) {
        const updatePreview = () => {
            const species = (speciesSelect.value || "").trim();
            const age = (ageInput.value || "").trim();

            if (!species || !age) {
                previewLink.setAttribute("href", "#");
                previewLink.setAttribute("aria-disabled", "true");
                previewLink.classList.add("is-disabled");
                return;
            }

            previewLink.setAttribute(
                "href",
                `/vaccines/recommendations?species=${encodeURIComponent(species)}&age=${encodeURIComponent(age)}`
            );
            previewLink.setAttribute("aria-disabled", "false");
            previewLink.classList.remove("is-disabled");
        };

        speciesSelect.addEventListener("change", updatePreview);
        ageInput.addEventListener("input", updatePreview);
        updatePreview();
    }

});
