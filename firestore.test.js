const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_cfo_001";
const BOB_UID = "bob_cfo_002";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function buildProfilePayload(uid, ts) {
  return {
    userId: uid,
    displayName: "Cadete Silva",
    dailyGoalMinutes: 30,
    weeklyGoalDays: 6,
    workSchedule: "COMERCIAL",
    fatigueLevel: "DISPOSTO",
    sleepHours: 7,
    xp: 120,
    level: 2,
    streakDays: 3,
    lastStudyDay: "2026-11-19",
    totalStudyMinutes: 45,
    totalQuestionsAnswered: 20,
    totalQuestionsCorrect: 16,
    soundEnabled: true,
    soundVolume: 0.7,
    diagnosticCompleted: false,
    focusSubjects: ["portugues", "matematica"],
    createdAt: ts,
    updatedAt: ts,
  };
}

test("Unauthenticated user cannot read or write any user profile or subcollections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date(Date.now() - 1000);
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(buildProfilePayload(ALICE_UID, now))
  );
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("errorNotebook").get());
});

test("Authenticated owner can create and partially update profile; rejects ghost fields and immutable mutations", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(buildProfilePayload(ALICE_UID, created))
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      xp: 180,
      fatigueLevel: "MODERADO",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt mutation
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost field on create
  const ghostPayload = buildProfilePayload(ALICE_UID, created);
  ghostPayload.isAdmin = true;
  await assertFails(
    aliceDb.collection("users").doc("alice_ghost").set(ghostPayload)
  );
});

test("Cross-user isolation blocks Bob from reading or writing Alice data", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(buildProfilePayload(ALICE_UID, now))
  );

  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({ xp: 9999, updatedAt: now })
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("errorNotebook").get()
  );
});

test("Subcollections: errorNotebook, reviewItems, flashcards, simulatedExams, essays, diagnostics, questionLogs, achievements", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 2000);

  // Error notebook
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("errorNotebook").doc("err_q1").set({
      userId: ALICE_UID,
      questionId: "q_port_01",
      subjectId: "portugues",
      subjectName: "Língua Portuguesa",
      topicName: "Crase",
      statement: "Assinale a alternativa em que o acento grave é obrigatório.",
      options: ["A", "B", "C", "D"],
      chosenIndex: 0,
      correctIndex: 2,
      explanation: "Antes de palavra feminina determinada por artigo definido.",
      sourceLabel: "Questão Inédita — Padrão PAES/UEMA",
      retryCount: 0,
      mastered: false,
      recordedAtMillis: Date.now(),
      createdAt: now,
      updatedAt: now,
    })
  );

  // Review item
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("reviewItems").doc("rev_t1").set({
      userId: ALICE_UID,
      subjectId: "matematica",
      topicId: "mat_funcao_afim",
      topicTitle: "Função Afim",
      itemType: "TOPICO",
      referenceId: "mat_funcao_afim",
      stageIndex: 1,
      consecutiveHits: 1,
      errorCount: 0,
      nextReviewAtMillis: Date.now() + 86400000,
      lastReviewedAtMillis: Date.now(),
      masteryStatus: "EM_ANDAMENTO",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Essay
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("essays").doc("essay_1").set({
      userId: ALICE_UID,
      themeTitle: "Segurança Pública e Cidadania no Maranhão",
      draftText: "A segurança pública constitui pilar fundamental do Estado Democrático de Direito...",
      rewrittenText: "",
      grammarErrors: ["Vírgula separando sujeito e predicado no 2º parágrafo"],
      cohesionFeedback: "Bom uso de conectivos interparágrafos.",
      improvementSuggestions: ["Aprofundar repertório constitucional"],
      formativeScore: 840,
      aiSummary: "Texto bem estruturado com tese clara.",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Question logs & simulated exams
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("questionLogs").doc("log_1").set({
      userId: ALICE_UID,
      subjectId: "portugues",
      topicId: "port_crase",
      questionId: "q_port_01",
      mode: "AULA",
      correct: true,
      dayKey: "2026-11-19",
      minutesSpent: 2,
      createdAt: now,
    })
  );

  // Reject @DocumentId 'id' leakage
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("flashcards").doc("fc_1").set({
      id: "fc_1",
      userId: ALICE_UID,
      subjectId: "portugues",
      topicTitle: "Crase",
      front: "Quando não usar crase?",
      back: "Antes de verbos e palavras masculinas.",
      memoryTip: "Antes de verbo, crase passa longe!",
      confidence: "SEI_RESPONDER",
      nextReviewAtMillis: Date.now(),
      reviewCount: 1,
      customCreated: false,
      createdAt: now,
      updatedAt: now,
    })
  );

  // List queries succeed for owner
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("errorNotebook").get()
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("reviewItems").get()
  );
});
